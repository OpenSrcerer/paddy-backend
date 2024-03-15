package online.danielstefani.paddy.user

import jakarta.enterprise.context.ApplicationScoped
import online.danielstefani.paddy.daemon.DaemonRepository

@ApplicationScoped
class UserService(
    private val userRepository: UserRepository,
    private val daemonRepository: DaemonRepository
) {

}