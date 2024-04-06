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
import online.danielstefani.paddy.power.PowerRepository
import online.danielstefani.paddy.schedule.ScheduleRepository
import online.danielstefani.paddy.user.UserRepository
import online.danielstefani.paddy.util.toMono
import org.eclipse.microprofile.rest.client.inject.RestClient
import reactor.core.publisher.Mono

@ApplicationScoped
class DaemonService(
    private val powerRepository: PowerRepository,
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

    fun createDaemon(
        username: String,
        daemonId: Long,
        recovery: Boolean = false
    ): Uni<CreateDaemonResponse?> {
        val user = userRepository.get(username)

        val daemonUni: Uni<Daemon?> =
            if (recovery)
                Uni.createFrom().emitter {
                    it.complete(daemonRepository.get("$daemonId", username))
                }
            else
                Uni.createFrom().emitter {
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
        resetDaemon(username, daemonId) ?: return null

        // Get schedules first
        val schedules = scheduleRepository.getAll(username, daemonId)

        // Delete all related entites from DB
        scheduleRepository.deleteAll(daemonId)
        powerRepository.deleteAll(daemonId)

        // Inform scheduler to remove tasks
        schedules.map { mqtt.publish(it.id!!.toString()) }
            .let { Flowable.concat(it) }
            .subscribe()

        // Delete the daemon itself
        return daemonRepository.deleteUserDaemon(daemonId)
    }

    /*
    Reset (deletes all credentials in Daemon).
    It also turns the device off.
     */
    fun resetDaemon(username: String, daemonId: String): Daemon? {
        val daemon = daemonRepository.get(daemonId, username) ?: return null

        val updateMono = Uni.createFrom().emitter { e ->
            e.complete(daemonRepository.update(daemonId) { it.lastPing = -1 })
        }.toMono()

        val resetPublish = mqtt.publish(daemonId, action = "reset", qos = MqttQos.EXACTLY_ONCE)
            ?.let { Mono.fromDirect(it) }

        // Turn reset device then turn to recovery
        resetPublish?.flatMap { updateMono }
            ?.subscribe()

        return daemon
    }

    fun toggleDaemon(daemonId: String): Boolean {
        daemonRepository.update(daemonId)
            { it.on = !it.on } ?: return false

        mqtt.publish(daemonId, action = "toggle", qos = MqttQos.EXACTLY_ONCE)
            ?.subscribe()

        return true
    }
}