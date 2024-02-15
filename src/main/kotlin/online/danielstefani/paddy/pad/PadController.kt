package online.danielstefani.paddy.pad

import io.quarkus.security.Authenticated
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.jboss.resteasy.reactive.RestPath
import java.util.*

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class PadController(
    private val padRepository: PadRepository
) {

    @POST
    @Path("/pad/{serial}")
    @Authenticated
    fun createDevice(@RestPath serial: String): Pad {
        val deviceSerial = serial.ifEmpty { UUID.randomUUID().toString() }
        val jwt = "123456789"

        return padRepository.upsert(deviceSerial, jwt)
    }
}

