package online.danielstefani.paddy.user

import jakarta.enterprise.context.ApplicationScoped
import online.danielstefani.paddy.pad.Pad
import online.danielstefani.paddy.pad.PadRepository
import org.neo4j.ogm.session.queryForObject

@ApplicationScoped
class UserService(
    private val userRepository: UserRepository,
    private val padRepository: PadRepository
) {
    fun getAllUserPads(username: String): List<Pad> {
        val user = userRepository.get(username)
        return padRepository.getAllUserPads(user!!)
    }
}