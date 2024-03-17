package online.danielstefani.paddy.mqtt

import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class MqttTopicRouter(
    private val mqttController: MqttController
) {
    private val router: HashMap<String, (Mqtt5Publish) -> Unit> = hashMapOf(

    )

    fun route(message: Mqtt5Publish) {

    }

    fun route(throwable: Throwable) {

    }
}