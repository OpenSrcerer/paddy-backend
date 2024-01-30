package online.danielstefani.paddy.client

import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import com.hivemq.client.mqtt.mqtt5.Mqtt5RxClient
import com.hivemq.client.mqtt.mqtt5.advanced.Mqtt5ClientAdvancedConfig
import com.hivemq.client.mqtt.mqtt5.message.auth.Mqtt5SimpleAuth
import com.hivemq.client.mqtt.mqtt5.message.subscribe.Mqtt5Subscription
import io.quarkus.logging.Log
import io.quarkus.runtime.StartupEvent
import io.reactivex.Completable
import io.reactivex.Flowable
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import online.danielstefani.paddy.controllers.MqttController
import java.util.*
import java.util.concurrent.TimeUnit

@ApplicationScoped
class RxMqttClient(
    private val mqttConfig: MqttConfiguration,
    private val mqttController: MqttController
) {
    // Build the client on startup
    fun startup(@Observes event: StartupEvent) {
        rebuildMqttClient()
    }

    // Singleton
    private var mqttClient: Mqtt5RxClient? = null

    /**
     * Rebuilds (replaces) current singleton client.
     */
    fun rebuildMqttClient(): Mqtt5RxClient {
        shutdownClient().blockingAwait() // Kill current client (null checks inside)

        // ---- Build Client ----
        val client = Mqtt5Client.builder()
            .identifier(
                "${mqttConfig.clientId()}-${UUID.randomUUID()}".apply {
                    Log.info("[client->mqtt->reaper] // Building new MQTT client: $this")
                }
            )
            .serverHost(mqttConfig.host())
            .serverPort(mqttConfig.port())
            .also {
                if (mqttConfig.username().isNotBlank() && mqttConfig.password().isNotBlank()) {
                    it.simpleAuth(
                        Mqtt5SimpleAuth.builder()
                            .username(mqttConfig.username())
                            .password(mqttConfig.password().toByteArray(Charsets.UTF_8))
                            .build()
                    )
                }
            }
            .advancedConfig(
                Mqtt5ClientAdvancedConfig.builder()
                    .allowServerReAuth(true)
                    .build()
            )
            .buildRx()

        return client.apply {
            mqttClient = this
            mqttClient!!
                .connectScenario()
                .applySubscription()
        }
    }

    /**
     * Define connection rules for MQTT Client building above.
     */
    private fun Mqtt5RxClient.connectScenario(): Mqtt5RxClient {
        this.connectWith()
            .noSessionExpiry()
            .cleanStart(true)
            .applyConnect()
            .doOnSuccess { Log.info("[client->mqtt] // " +
                    "Connected to ${mqttConfig.host()}:${mqttConfig.port()}, ${it.reasonCode}") }
            .doOnError { Log.error("[client->mqtt] // " +
                    "Connection failed to ${mqttConfig.host()}:${mqttConfig.port()}, ${it.message}") }
            .doOnSubscribe { Log.info("[client->mqtt] // " +
                    "Connecting to... ${mqttConfig.host()}:${mqttConfig.port()}")
            }
            .retryWhen {
                Flowable.timer(30, TimeUnit.SECONDS)
                    .doOnNext { Log.info("[client->mqtt] // " +
                            "Retrying connection to ${mqttConfig.host()}:${mqttConfig.port()}...") }
            }
            .ignoreElement()
            .blockingAwait()

        return mqttClient!!
    }

    /**
     * Subscribe to the given topics.
     */
    private fun Mqtt5RxClient.applySubscription() {
        this.subscribePublishesWith()
            .addSubscriptions(
                mqttConfig.getSubscriptions()
                    .map {
                        Mqtt5Subscription.builder()
                            .topicFilter(it)
                            .qos(MqttQos.AT_LEAST_ONCE)
                            .build()
                    }
            )
            .applySubscribe()
            .doOnError { mqttController.readError(it) }
            .doOnNext { mqttController.readMessage(it) }
            .doOnSubscribe {
                Log.info("[client->mqtt] // " + "Subscribing to topics [" +
                        "${mqttConfig.getSubscriptions().joinToString(", ") { "'${it}'" }}]")
            }
            .subscribe()
    }

    private fun shutdownClient(): Completable {
        if (mqttClient == null || !mqttClient!!.state.isConnected)
            return Completable.complete()
                .doOnComplete { Log.info("[client->mqtt->reaper] // " +
                        "Old MQTT client was null or disconnected, ignoring it") }

        return mqttClient!!.disconnect()
            .doOnComplete { Log.info("[client->mqtt->reaper] // " +
                    "Reaped old MQTT client ${mqttClient!!.config.clientIdentifier.get()}") }
    }
}