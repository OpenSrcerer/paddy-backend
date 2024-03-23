package online.danielstefani.paddy.mqtt

import com.hivemq.client.mqtt.datatypes.MqttQos
import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped
import online.danielstefani.paddy.daemon.DaemonRepository
import online.danielstefani.paddy.daemon.DaemonService
import java.time.Instant

@ApplicationScoped
class MqttController(
    private val mqtt: RxMqttClient,
    private val daemonService: DaemonService,
    private val daemonRepository: DaemonRepository
) {
    @DaemonAction("ping")
    fun ping(daemonId: String, body: String?) {
        val on = daemonService.getDaemon(daemonId)?.on ?: return

        daemonRepository.update(daemonId) {
            it.lastPing = Instant.now().epochSecond
        }

        mqtt.publish(daemonId, action = if (on) "on" else "off", qos = MqttQos.EXACTLY_ONCE)
            ?.subscribe()
    }

    @DaemonAction
    fun unhandled(daemonId: String, body: String?) {
        Log.info("Unhandled message received: <$body>")
    }
}