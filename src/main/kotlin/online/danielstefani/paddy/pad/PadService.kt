package online.danielstefani.paddy.pad

import jakarta.enterprise.context.ApplicationScoped
import online.danielstefani.paddy.mqtt.RxMqttClient
import online.danielstefani.paddy.user.UserRepository

@ApplicationScoped
class PadService(
    private val padRepository: PadRepository,
    private val userRepository: UserRepository,
    private val rxMqttClient: RxMqttClient
) {
    fun getAllUserPads(username: String): List<Pad> {
        val user = userRepository.get(username)
        return padRepository.getAllUserPads(user!!)
    }

    fun createPad(username: String): Pad {
        val user = userRepository.get(username)
        return padRepository.createUserPad(user!!)
    }

    fun deletePad(username: String, padId: String): Pad? {
        val user = userRepository.get(username)
        return padRepository.deleteUserPad(user!!, padId)
    }

    fun togglePad(username: String, padId: String): Boolean {
        val user = userRepository.get(username)
        val pad = padRepository.getUserPad(user!!, padId)

        if (pad == null) return false

        rxMqttClient.publish("${RxMqttClient.DEVICE_READS_TOPIC}/$padId", "toggle")
            ?.subscribe()
        return true
    }
}