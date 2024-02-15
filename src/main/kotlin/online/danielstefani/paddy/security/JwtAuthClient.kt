package online.danielstefani.paddy.security

import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
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

    @GET
    @Path("/admin-jwt")
    fun getAdminJwt(): String

    @POST
    @Path("/validate")
    fun validateJwt(request: AuthorizationRequestDto): Uni<AuthorizationResultDto>

}