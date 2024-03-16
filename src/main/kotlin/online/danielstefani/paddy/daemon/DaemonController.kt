package online.danielstefani.paddy.daemon

import io.quarkus.security.Authenticated
import io.quarkus.security.identity.SecurityIdentity
import io.smallrye.mutiny.Uni
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import online.danielstefani.paddy.daemon.dto.CreateDaemonResponse
import online.danielstefani.paddy.util.username
import org.jboss.resteasy.reactive.RestPath
import org.jboss.resteasy.reactive.RestResponse
import org.jboss.resteasy.reactive.RestResponse.*

@Path("/daemon")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
class DaemonController(
    private val daemonService: DaemonService,
    private val securityIdentity: SecurityIdentity
) {

    @GET
    @Path("/{id}")
    fun getDaemon(@RestPath id: String): String {
        return ":) Not Implemented Yet"
    }

    @GET
    fun getAllUserDaemons(): List<Daemon> {
        return daemonService.getAllUserDaemons(securityIdentity.username())
    }

    @POST
    @Path("/{id}")
    fun postDaemon(@RestPath id: String): Uni<RestResponse<CreateDaemonResponse>> {
        val daemonId = try { id.toLong() } catch (e: NumberFormatException) {
            return Uni.createFrom().item(status(Response.Status.BAD_REQUEST))
        }

        return daemonService.createDaemon(securityIdentity.username(), daemonId)
            .map { ResponseBuilder.ok(it).build() }
    }

    @PATCH
    @Path("/{id}")
    fun patchDaemon(@RestPath id: String): String {
        return ":) Not Implemented Yet"
    }

    @DELETE
    @Path("/{id}")
    fun deleteDaemon(@RestPath id: String): RestResponse<Daemon> {
        return daemonService.deleteDaemon(securityIdentity.username(), id)?.let { ResponseBuilder.ok(it).build() }
            ?: status(Response.Status.NOT_FOUND)
    }

    @PATCH
    @Path("/{id}/toggle")
    fun toggleDaemon(@RestPath id: String): RestResponse<Unit> {
        return if (daemonService.toggleDaemon(securityIdentity.username(), id)) ok()
        else notFound()
    }

    // ---- Statistics ----
    @GET
    @Path("/{id}/statistic")
    fun getDaemonStatistic(@RestPath id: String): String {
        return ":) Not Implemented Yet"
    }

    @PUT
    @Path("/{id}/statistic")
    fun putDaemonStatistic(@RestPath id: String): String {
        return ":) Not Implemented Yet"
    }
}

