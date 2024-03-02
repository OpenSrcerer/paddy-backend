package online.danielstefani.paddy.pad

import io.quarkus.security.Authenticated
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.GET
import jakarta.ws.rs.PATCH
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.jboss.resteasy.reactive.RestPath

@Path("/pad")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
class PadController(
    private val padRepository: PadRepository
) {

    @GET
    @Path("/{serial}")
    @Authenticated
    fun getPad(@RestPath serial: String): String {
        return ":) Not Implemented Yet"
    }

    @POST
    @Path("/{serial}")
    fun postPad(@RestPath serial: String): String {
//        val deviceSerial = serial.ifEmpty { UUID.randomUUID().toString() }
//        val jwt = "123456789"
//
//        return padRepository.upsert(deviceSerial, jwt)
        return ":) Not Implemented Yet"
    }

    @PATCH
    @Path("/{serial}")
    fun patchPad(@RestPath serial: String): String {
        return ":) Not Implemented Yet"
    }

    @DELETE
    @Path("/{serial}")
    fun deletePad(@RestPath serial: String): String {
        return ":) Not Implemented Yet"
    }

    // ---- Statistics ----
    @GET
    @Path("/{serial}/statistic")
    fun getPadStatistic(@RestPath serial: String): String {
        return ":) Not Implemented Yet"
    }

    @PUT
    @Path("/{serial}/statistic")
    fun putPadStatistic(@RestPath serial: String): String {
        return ":) Not Implemented Yet"
    }
}

