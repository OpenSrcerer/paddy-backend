package online.danielstefani.paddy.daemon

import com.hivemq.client.mqtt.datatypes.MqttQos
import io.reactivex.Flowable
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import online.danielstefani.paddy.daemon.dto.CreateDaemonResponse
import online.danielstefani.paddy.jwt.JwtAuthClient
import online.danielstefani.paddy.jwt.dto.JwtRequestDto
import online.danielstefani.paddy.jwt.dto.JwtType
import online.danielstefani.paddy.mqtt.RxMqttClient
import online.danielstefani.paddy.schedule.ScheduleRepository
import online.danielstefani.paddy.user.UserRepository
import org.eclipse.microprofile.rest.client.inject.RestClient

@ApplicationScoped
class DaemonService(
    private val scheduleRepository: ScheduleRepository,
    private val daemonRepository: DaemonRepository,
    private val userRepository: UserRepository,
    private val mqtt: RxMqttClient,
    @RestClient private val paddyAuth: JwtAuthClient
) {
    fun getDaemon(daemonId: String): Daemon? {
        return daemonRepository.get(daemonId)
    }

    fun getAllUserDaemons(username: String): List<Daemon> {
        val user = userRepository.get(username)
        return daemonRepository.getAllUserDaemons(user!!)
    }

    fun createDaemon(username: String, daemonId: Long): Uni<CreateDaemonResponse?> {
        val user = userRepository.get(username)

        val daemonUni = Uni.createFrom().emitter<Daemon> {
            it.complete(daemonRepository.createUserDaemon("$daemonId", user!!))
        }
        val jwtUni = paddyAuth.generateJwt(JwtRequestDto("$daemonId", JwtType.DAEMON))

        return Uni.combine().all().unis(daemonUni, jwtUni)
            .with { daemon, jwtRes ->
                if (daemon != null) CreateDaemonResponse(daemon, jwtRes.jwt)
                else null
            }
    }

    fun deleteDaemon(username: String, daemonId: String): Daemon? {
        // Get schedules first
        val schedules = scheduleRepository.getAll(username, daemonId)

        // Delete all schedules from DB
        scheduleRepository.deleteAll(daemonId)

        // Inform scheduler to remove tasks
        schedules.map { mqtt.publish(it.id!!.toString()) }
            .let { Flowable.concat(it) }
            // Also turn the device off
            .concatWith(mqtt.publish(daemonId, action = "off", qos = MqttQos.EXACTLY_ONCE))
            .subscribe()

        // Delete the daemon itself
        return daemonRepository.deleteUserDaemon(daemonId)
    }

    fun toggleDaemon(daemonId: String): Boolean {
        daemonRepository.update(daemonId)
            { it.on = !it.on } ?: return false

        mqtt.publish(daemonId, action = "toggle", qos = MqttQos.EXACTLY_ONCE)
            ?.subscribe()

        return true
    }
}