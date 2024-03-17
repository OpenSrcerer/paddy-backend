package online.danielstefani.paddy.mqtt

import com.hivemq.client.mqtt.datatypes.MqttQos
import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped
import online.danielstefani.paddy.daemon.DaemonService

@ApplicationScoped
class MqttController(
    private val mqtt: RxMqttClient,
    private val daemonService: DaemonService
) {
    @DaemonAction("ping")
    fun ping(daemonId: String, body: String?) {
        val on = daemonService.getDaemon(daemonId)?.on ?: return

        mqtt.publish(daemonId, if (on) "1" else "0", MqttQos.EXACTLY_ONCE)
            ?.subscribe()
    }

    @DaemonAction
    fun unhandled(daemonId: String, body: String?) {
        Log.info("Unhandled message received: <$body>")
    }
}