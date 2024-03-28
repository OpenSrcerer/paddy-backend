package online.danielstefani.paddy.power

import io.quarkus.security.Authenticated
import io.quarkus.security.identity.SecurityIdentity
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import online.danielstefani.paddy.util.username
import org.jboss.resteasy.reactive.RestPath
import org.jboss.resteasy.reactive.RestQuery

@Path("/daemon/{daemonId}/power")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
class PowerController(
    private val powerRepository: PowerRepository,
    private val securityIdentity: SecurityIdentity
) {
    @GET
    fun getAllPowers(
        @RestPath daemonId: String,
        @RestQuery limit: Int = 10,
        @RestQuery before: Long? = null,
        @RestQuery after: Long? = null
    ): List<Power> {
        return powerRepository.getAllBetween(
            daemonId, securityIdentity.username(), limit, before, after)
    }
}
