package online.danielstefani.paddy.jwt

import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import online.danielstefani.paddy.jwt.dto.JwtRequestDto
import online.danielstefani.paddy.jwt.dto.JwtResponseDto
import online.danielstefani.paddy.security.dto.AuthorizationRequestDto
import online.danielstefani.paddy.security.dto.AuthorizationResultDto
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient

/**
 * Retrieves tokens for the MQTT client to connect
 * by contacting the authentication server.
 */
@ApplicationScoped
@RegisterRestClient(configKey = "jwt")
@Path("/auth/v1")
interface JwtAuthClient {

    @POST
    @Path("/jwt/")
    fun generateJwt(dto: JwtRequestDto): Uni<JwtResponseDto>

    @POST
    @Path("/validate")
    fun validateJwt(request: AuthorizationRequestDto): Uni<AuthorizationResultDto>

}