package online.danielstefani.paddy.pad

import jakarta.enterprise.context.ApplicationScoped
import online.danielstefani.paddy.jwt.JwtAuthClient
import online.danielstefani.paddy.user.UserRepository
import org.eclipse.microprofile.rest.client.inject.RestClient

@ApplicationScoped
class PadService(
    private val padRepository: PadRepository,
    private val userRepository: UserRepository,
    @RestClient private val paddyAuth: JwtAuthClient
) {
    fun createPad(username: String): Pad {
        val user = userRepository.get(username)
        return padRepository.create(user!!)
    }
}