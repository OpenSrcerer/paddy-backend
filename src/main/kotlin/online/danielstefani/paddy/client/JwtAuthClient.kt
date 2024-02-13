package online.danielstefani.paddy.client

import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
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
    fun getJwt(): String
}