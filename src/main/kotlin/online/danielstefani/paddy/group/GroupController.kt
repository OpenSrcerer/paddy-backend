package online.danielstefani.paddy.group

import io.quarkus.security.Authenticated
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import org.jboss.resteasy.reactive.RestPath

@Path("/group")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
class GroupController() {

    @GET
    @Path("/{id}")
    fun getGroup(@RestPath id: String): String {
        return ":) Not Implemented Yet"
    }

    @POST
    fun postGroup(): String {
        return ":) Not Implemented Yet"
    }

    @PATCH
    @Path("/{id}")
    fun patchGroup(@RestPath id: String): String {
        return ":) Not Implemented Yet"
    }

    @DELETE
    @Path("/{id}")
    fun deleteGroup(@RestPath id: String): String {
        return ":) Not Implemented Yet"
    }
}