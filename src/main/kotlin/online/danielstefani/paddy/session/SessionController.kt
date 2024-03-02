package online.danielstefani.paddy.session

import io.smallrye.mutiny.Uni
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.NewCookie
import jakarta.ws.rs.core.Response
import online.danielstefani.paddy.jwt.JwtAuthClient
import online.danielstefani.paddy.jwt.dto.JwtRequestDto
import online.danielstefani.paddy.jwt.dto.JwtResponseDto
import online.danielstefani.paddy.jwt.dto.JwtType
import online.danielstefani.paddy.session.dto.LoginRequestDto
import online.danielstefani.paddy.user.UserRepository
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.resteasy.reactive.RestResponse
import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder;
import java.time.Instant
import java.util.*

@Path("/session")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class SessionController(
    private val userRepository: UserRepository,
    private val cookieConfig: JwtCookieConfig
) {
    @RestClient
    private lateinit var paddyAuth: JwtAuthClient

    /* Sets the paddy-jwt to the newly generated user jwt */
    @POST
    @Path("/login")
    fun login(dto: LoginRequestDto): Uni<RestResponse<Unit>> {
        val user = userRepository.getForLogin(dto)
            ?: return Uni.createFrom().item(RestResponse.status(Response.Status.FORBIDDEN))

        return paddyAuth.generateJwt(JwtRequestDto(user.username!!, JwtType.USER))
            .map { ResponseBuilder.ok<Unit>().cookie(it.buildCookie()).build() }
    }

    /* Clears the paddy-jwt */
    @POST
    @Path("/logout")
    fun logout(): Uni<RestResponse<Unit>> {
        return Uni.createFrom().item(JwtResponseDto("", Instant.now().epochSecond))
            .map { ResponseBuilder.ok<Unit>().cookie(it.buildCookie()).build() }
    }

    private fun JwtResponseDto.buildCookie(): NewCookie {
        return NewCookie.Builder(cookieConfig.name())
            .path(cookieConfig.path())
            .domain(cookieConfig.domain())
            .sameSite(NewCookie.SameSite.STRICT)
            .secure(true)
            .httpOnly(true)
            .value(this.jwt)
            .expiry(Date.from(Instant.ofEpochSecond(this.absoluteExpiryUnixSeconds)))
            .build()
    }
}