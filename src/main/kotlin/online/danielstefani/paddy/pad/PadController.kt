package online.danielstefani.paddy.pad

import io.quarkus.security.Authenticated
import io.quarkus.security.identity.SecurityIdentity
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import online.danielstefani.paddy.util.username
import org.jboss.resteasy.reactive.RestPath
import org.jboss.resteasy.reactive.RestResponse
import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder

@Path("/pad")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
class PadController(
    private val padRepository: PadRepository,
    private val padService: PadService,
    private val securityIdentity: SecurityIdentity
) {

    @GET
    @Path("/{id}")
    fun getPad(@RestPath id: String): String {
        return ":) Not Implemented Yet"
    }

    @GET
    fun getAllUserPads(): List<Pad> {
        return padService.getAllUserPads(securityIdentity.username())
    }

    @POST
    fun postPad(): Pad {
        return padService.createPad(securityIdentity.username())
    }

    @PATCH
    @Path("/{id}")
    fun patchPad(@RestPath id: String): String {
        return ":) Not Implemented Yet"
    }

    @DELETE
    @Path("/{id}")
    fun deletePad(@RestPath id: String): RestResponse<Pad> {
        return padService.deletePad(securityIdentity.username(), id)?.let { ResponseBuilder.ok(it).build() }
            ?: RestResponse.status(Response.Status.NOT_FOUND)

    }

    // ---- Statistics ----
    @GET
    @Path("/{id}/statistic")
    fun getPadStatistic(@RestPath id: String): String {
        return ":) Not Implemented Yet"
    }

    @PUT
    @Path("/{id}/statistic")
    fun putPadStatistic(@RestPath id: String): String {
        return ":) Not Implemented Yet"
    }
}

