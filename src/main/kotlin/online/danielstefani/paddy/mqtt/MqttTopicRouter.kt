package online.danielstefani.paddy.mqtt

import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish
import io.quarkus.logging.Log
import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.context.control.ActivateRequestContext
import jakarta.enterprise.event.Observes
import java.nio.charset.StandardCharsets
import kotlin.reflect.full.declaredMemberFunctions

/*
The Daemon MQTT topics are organized in such a manner:
daemon/{id}/v1/reads/ACTION

The point of this class is to extract ACTION and route it to the proper handler.
 */
@ApplicationScoped
class MqttTopicRouter(
    private val mqttController: MqttController
) {
    companion object {
        private const val FALLBACK_ACTION = "unhandled"
    }

    private val router: HashMap<String, (args: Array<Any?>) -> Unit> = HashMap()

    // Reflectively get all the functions in the class and add them to the router
    // if they are valid
    fun startup(@Observes event: StartupEvent) {
        MqttController::class.declaredMemberFunctions
            .also { Log.info("[mqtt->router] Found ${it.size} functions in controller.") }
            .filter { funx ->
                funx.annotations.map { it.annotationClass }.contains(DaemonAction::class) &&
                funx.parameters.size == 3 && // There is an extra parameter at parameters[0]
                funx.parameters[1].type.classifier == String::class &&
                funx.parameters[2].type.classifier == String::class
            }
            .also {
                Log.info("[mqtt->router] Adding ${it.size} " +
                        "valid functions for routing: ${it.map { f -> f.name }}")
            }
            .forEach { funx ->
                val action = (funx.annotations[0] as DaemonAction)
                    .action.ifEmpty { FALLBACK_ACTION }
                    .lowercase()
                router[action] = { args -> funx.call(mqttController, *args) }
            }
    }

    /*
    Request context needs to be activated in order to retrieve a
    request-scoped Neo4j session.
     */
    @ActivateRequestContext
    fun route(message: Mqtt5Publish) {
        val actionFunction = if (message.topic.levels.size < 5)
            router[FALLBACK_ACTION]!!
        else
            (router[message.topic.levels.last().lowercase()] ?: router[FALLBACK_ACTION])!!

        val daemonId = message.topic.levels[1]
        val payload = message.payload.let { payload ->
            if (payload.isPresent) payload.map { StandardCharsets.UTF_8.decode(it).toString() }.get()
            else null
        }

        try {
            actionFunction(arrayOf(daemonId, payload))
        } catch (ex: Exception) {
            route(ex)
        }
    }

    fun route(throwable: Throwable) {
        Log.error("[mqtt->router] Error while ingesting MQTT Message", throwable)
    }
}