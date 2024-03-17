package online.danielstefani.paddy.mqtt

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

    }
}