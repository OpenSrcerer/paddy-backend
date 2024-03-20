package online.danielstefani.paddy.security

import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class HttpSecurityService(
    private val securityRepository: HttpSecurityRepository
) {
    fun hasAccessToDaemon(username: String, daemonId: String): Uni<Boolean> {
        return securityRepository.getUserDaemon(username, daemonId)
            .map { it != null }
    }
}