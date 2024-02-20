package online.danielstefani.paddy.user

import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import online.danielstefani.paddy.user.dto.SignupRequestDto
import org.jboss.resteasy.reactive.RestResponse

@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class UserController(
    private val userRepository: UserRepository
) {

    @POST
    @Path("/signup")
    fun login(dto: SignupRequestDto): RestResponse<User> {
        val user = userRepository.create(dto.email, dto.username, dto.passwordHash)
            ?: return RestResponse.status(Response.Status.CONFLICT)

        return RestResponse.status(Response.Status.CREATED, user)
    }

}