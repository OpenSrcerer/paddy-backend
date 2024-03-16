package online.danielstefani.paddy.daemon

import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import online.danielstefani.paddy.daemon.dto.CreateDaemonResponse
import online.danielstefani.paddy.jwt.JwtAuthClient
import online.danielstefani.paddy.jwt.dto.JwtRequestDto
import online.danielstefani.paddy.jwt.dto.JwtType
import online.danielstefani.paddy.mqtt.RxMqttClient
import online.danielstefani.paddy.user.UserRepository
import org.eclipse.microprofile.rest.client.inject.RestClient

@ApplicationScoped
class DaemonService(
    private val daemonRepository: DaemonRepository,
    private val userRepository: UserRepository,
    private val rxMqttClient: RxMqttClient,
    @RestClient private val paddyAuth: JwtAuthClient
) {
    fun getAllUserDaemons(username: String): List<Daemon> {
        val user = userRepository.get(username)
        return daemonRepository.getAllUserDaemons(user!!)
    }

    fun createDaemon(username: String, daemonId: Long): Uni<CreateDaemonResponse?> {
        val user = userRepository.get(username)

        val daemonUni = Uni.createFrom().item { daemonRepository.createUserDaemon(user!!, daemonId) }
        val jwtUni = paddyAuth.generateJwt(JwtRequestDto("$daemonId", JwtType.DAEMON))

        return Uni.combine().all().unis(daemonUni, jwtUni)
            .with { daemon, jwtRes ->
                if (daemon != null) CreateDaemonResponse(daemon, jwtRes.jwt)
                else null
            }
    }

    fun deleteDaemon(username: String, daemonId: String): Daemon? {
        val user = userRepository.get(username)
        return daemonRepository.deleteUserDaemon(user!!, daemonId)
    }

    fun toggleDaemon(username: String, daemonId: String): Boolean {
        val user = userRepository.get(username)
        val daemon = daemonRepository.getUserDaemon(user!!, daemonId)
            ?: return false

        rxMqttClient.publish("${RxMqttClient.DEVICE_READS_TOPIC}/$daemonId", "toggle")
            ?.subscribe()
        return true
    }
}