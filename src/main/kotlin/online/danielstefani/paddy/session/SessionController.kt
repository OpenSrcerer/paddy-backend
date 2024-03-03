package online.danielstefani.paddy.session

import io.smallrye.mutiny.Uni
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import online.danielstefani.paddy.jwt.JwtAuthClient
import online.danielstefani.paddy.jwt.dto.JwtRequestDto
import online.danielstefani.paddy.jwt.dto.JwtResponseDto
import online.danielstefani.paddy.jwt.dto.JwtType
import online.danielstefani.paddy.session.dto.LoginRequestDto
import online.danielstefani.paddy.user.UserRepository
import online.danielstefani.paddy.util.isPasswordHashMatch
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.resteasy.reactive.RestResponse
import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder

@Path("/session")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class SessionController(
    private val userRepository: UserRepository,
    @RestClient private val paddyAuth: JwtAuthClient
) {
    /* Sets the paddy-jwt to the newly generated user jwt */
    @POST
    @Path("/login")
    fun login(dto: LoginRequestDto): Uni<RestResponse<JwtResponseDto>> {
        val user = userRepository.get(dto.emailOrUsername)
            ?: return Uni.createFrom().item(RestResponse.status(Response.Status.NOT_FOUND))

        if (!isPasswordHashMatch(dto.passwordHash, user.passwordHash!!, user.passwordSalt!!))
            return Uni.createFrom().item(RestResponse.status(Response.Status.FORBIDDEN))

        return paddyAuth.generateJwt(JwtRequestDto(user.username!!, JwtType.USER))
            .map { ResponseBuilder.ok<JwtResponseDto>(it).build() }
    }
}