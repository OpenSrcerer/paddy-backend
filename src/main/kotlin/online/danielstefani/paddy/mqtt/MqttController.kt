package online.danielstefani.paddy.mqtt

import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish
import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class MqttController(
    private val rxMqttClient: RxMqttClient
) {

    fun readMessage(message: Mqtt5Publish) {
        rxMqttClient.publish("device-reads", String(message.payloadAsBytes))
            ?.subscribe()
    }

    fun readError(error: Throwable) {
        Log.error(error)
    }
}