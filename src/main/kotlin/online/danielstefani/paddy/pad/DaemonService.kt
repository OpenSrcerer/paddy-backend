package online.danielstefani.paddy.pad

import jakarta.enterprise.context.ApplicationScoped
import online.danielstefani.paddy.mqtt.RxMqttClient
import online.danielstefani.paddy.user.UserRepository

@ApplicationScoped
class DaemonService(
    private val daemonRepository: DaemonRepository,
    private val userRepository: UserRepository,
    private val rxMqttClient: RxMqttClient
) {
    fun getAllUserDaemons(username: String): List<Daemon> {
        val user = userRepository.get(username)
        return daemonRepository.getAllUserDaemons(user!!)
    }

    fun createDaemon(username: String): Daemon {
        val user = userRepository.get(username)
        return daemonRepository.createUserDaemon(user!!)
    }

    fun deleteDaemon(username: String, daemonId: String): Daemon? {
        val user = userRepository.get(username)
        return daemonRepository.deleteUserDaemon(user!!, daemonId)
    }

    fun toggleDaemon(username: String, daemonId: String): Boolean {
        val user = userRepository.get(username)
        val daemon = daemonRepository.getUserDaemon(user!!, daemonId)

        if (daemon == null) return false

        rxMqttClient.publish("${RxMqttClient.DEVICE_READS_TOPIC}/$daemonId", "toggle")
            ?.subscribe()
        return true
    }
}