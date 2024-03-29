package online.danielstefani.paddy.user

import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import online.danielstefani.paddy.user.dto.SignupRequestDto
import online.danielstefani.paddy.util.generatePBKHashBase64
import org.jboss.resteasy.reactive.RestResponse


@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class UserController(
    private val userRepository: UserRepository
) {
    /*
    Hash the given password hash with a salt, the signup the user.
    It is expected that the given PasswordHash is a PBKDF2 hashed
    with no salt.
     */
    @POST
    @Path("/signup")
    fun signup(dto: SignupRequestDto): RestResponse<User> {
        val (hx2Password, salt) = generatePBKHashBase64(dto.passwordHash)

        val user = userRepository.create(dto.email, dto.username, hx2Password, salt)
            ?: return RestResponse.status(Response.Status.CONFLICT)

        return RestResponse.status(Response.Status.CREATED, user)
    }
}