package online.danielstefani.paddy.session

import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

@Path("/session")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class SessionController {

    @POST
    @Path("/login")
    fun login(): String {
        return ":)"
    }

    @POST
    @Path("/logout")
    fun logout(): String {
        return ":)"
    }

}