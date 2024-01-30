package online.danielstefani.paddy.controllers

import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish
import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class MqttController {

    fun readMessage(message: Mqtt5Publish) {
        Log.info(String(message.payloadAsBytes))
    }

    fun readError(error: Throwable) {
        Log.error(error)
    }
}