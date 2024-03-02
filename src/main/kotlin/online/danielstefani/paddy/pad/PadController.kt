package online.danielstefani.paddy.pad

import io.quarkus.security.Authenticated
import io.quarkus.security.identity.SecurityIdentity
import io.quarkus.security.runtime.QuarkusSecurityIdentity
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.GET
import jakarta.ws.rs.PATCH
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import online.danielstefani.paddy.util.username
import org.jboss.resteasy.reactive.RestPath
import org.jboss.resteasy.reactive.RestResponse

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
    fun deletePad(@RestPath id: String): String {
        return ":) Not Implemented Yet"
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

