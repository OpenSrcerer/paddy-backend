package online.danielstefani.paddy.power

import io.quarkus.security.Authenticated
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import org.jboss.resteasy.reactive.RestPath
import org.jboss.resteasy.reactive.RestQuery

@Path("/daemon/{daemonId}/power")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
class PowerController(
    private val powerRepository: PowerRepository
) {
    @GET
    fun getAllPowers(
        @RestPath daemonId: String,
        @RestQuery @DefaultValue("10") limit: Int,
        @RestQuery before: Long? = null,
        @RestQuery after: Long? = null
    ): List<Power> {
        return powerRepository.getAllBetween(daemonId, limit, before, after)
    }
}
