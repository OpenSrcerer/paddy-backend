package online.danielstefani.paddy

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path

@Path("/health")
class HealthController {

    @GET
    fun health(): String {
        return ":)"
    }
}