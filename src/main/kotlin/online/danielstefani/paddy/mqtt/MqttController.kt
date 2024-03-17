package online.danielstefani.paddy.mqtt

import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class MqttController(
    private val mqtt: RxMqttClient
) {
    @DaemonAction("hello")
    fun hello(daemonId: String, body: String?) {
        mqtt.publish(daemonId, "hello back!")
            ?.subscribe()
    }

    @DaemonAction
    fun unhandled(daemonId: String, body: String?) {
        Log.info("Unhandled message received: <$body>")
    }
}