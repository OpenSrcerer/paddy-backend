package online.danielstefani.paddy.daemon

import io.quarkus.security.Authenticated
import io.quarkus.security.identity.SecurityIdentity
import io.smallrye.mutiny.Uni
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
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
    fun getUserDaemon(@RestPath id: Long): RestResponse<Daemon?> {
        return daemonService.getDaemon(id.toString())
            ?.let { ok(it) }
            ?: status(Response.Status.NOT_FOUND)
    }

    @GET
    fun getAllUserDaemons(): List<Daemon> {
        return daemonService.getAllUserDaemons(securityIdentity.username())
    }

    @POST
    fun postDaemon(
        @NotNull @Valid daemon: Daemon
    ): Uni<RestResponse<CreateDaemonResponse?>> {
        return daemonService.createDaemon(securityIdentity.username(), daemon.id!!.toLong())
            .map {
                if (it != null) ResponseBuilder.ok(it).build()
                else status(Response.Status.CONFLICT)
            }
    }

    @PATCH
    @Path("/{id}/toggle")
    fun toggleDaemon(@RestPath id: Long): RestResponse<Unit> {
        return if (daemonService.toggleDaemon(id.toString())) ok()
        else notFound()
    }

    @PATCH
    @Path("/{id}/recover")
    fun recoverDaemon(
        @NotNull @Valid daemon: Daemon
    ): Uni<RestResponse<CreateDaemonResponse?>> {
        return daemonService.createDaemon(securityIdentity.username(), daemon.id!!.toLong(), true)
            .map {
                if (it != null) ResponseBuilder.ok(it).build()
                else status(Response.Status.CONFLICT)
            }
    }

    @PATCH
    @Path("/{id}/reset")
    fun resetDaemon(@RestPath id: Long): RestResponse<Daemon> {
        return daemonService.resetDaemon(securityIdentity.username(), id.toString())
            ?.let { ok(it) }
            ?: status(Response.Status.NOT_FOUND)
    }

    @PATCH
    @Path("/{id}")
    fun patchDaemon(@RestPath id: Long): String {
        return ":) Not Implemented Yet"
    }

    @DELETE
    @Path("/{id}")
    fun deleteDaemon(@RestPath id: Long): RestResponse<Daemon> {
        return daemonService.deleteDaemon(securityIdentity.username(), id.toString())
            ?.let { ok(it) }
            ?: status(Response.Status.NOT_FOUND)
    }
}

