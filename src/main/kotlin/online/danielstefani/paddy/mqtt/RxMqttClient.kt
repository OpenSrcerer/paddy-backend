package online.danielstefani.paddy.mqtt

import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import com.hivemq.client.mqtt.mqtt5.Mqtt5RxClient
import com.hivemq.client.mqtt.mqtt5.advanced.Mqtt5ClientAdvancedConfig
import com.hivemq.client.mqtt.mqtt5.message.auth.Mqtt5SimpleAuth
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PublishResult
import com.hivemq.client.mqtt.mqtt5.message.subscribe.Mqtt5Subscription
import io.quarkus.logging.Log
import io.quarkus.runtime.StartupEvent
import io.reactivex.*
import io.reactivex.Observable
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import online.danielstefani.paddy.jwt.JwtAuthClient
import online.danielstefani.paddy.jwt.dto.JwtRequestDto
import online.danielstefani.paddy.jwt.dto.JwtType
import online.danielstefani.paddy.util.toMono
import org.eclipse.microprofile.rest.client.inject.RestClient
import reactor.core.publisher.Flux
import reactor.util.retry.Retry
import java.time.Duration
import java.util.*

@ApplicationScoped
class RxMqttClient(
    private val mqttConfig: MqttConfiguration,
    private val mqttRouter: MqttTopicRouter,
    @RestClient private val paddyAuth: JwtAuthClient
) {
    private var mqttClient: Mqtt5RxClient? = null // Singleton
    private val mqttClientId = UUID.randomUUID()

    private var jwtUsername: String? = null

    // Build the client on startup
    fun startup(@Observes event: StartupEvent) {

        // Try to get the auth token first
        paddyAuth.generateJwt(JwtRequestDto("paddy-backend", JwtType.ADMIN)).toMono()
            .map { it.jwt }
            .doOnSubscribe { Log.info("[client->mqtt] Retrieving JWT to connect to broker...") }
            .doOnSuccess { Log.info("[client->mqtt] Got JWT <${it.slice(0..10)}...> connecting to broker!") }
            .doOnError { Log.error("[client->mqtt] Failed to retrieve JWT!", it) }
            .retryWhen(Retry.fixedDelay(Long.MAX_VALUE, Duration.ofSeconds(30)))
            .subscribe {
                jwtUsername = it
                rebuildMqttClient()
            }
    }

    fun publish(scheduleId: String): Flowable<Mqtt5PublishResult>? {
        val topic = mqttConfig.schedulerEvents()
            .replace("\$share/scheduler-cluster/", "")
            .replace("+", scheduleId)

        return publish(topic, "", MqttQos.EXACTLY_ONCE)
    }

    fun publish(
        daemonId: String,
        action: String,
        message: String = "",
        qos: MqttQos = MqttQos.AT_MOST_ONCE,
    ): Flowable<Mqtt5PublishResult>? {
        val topic = mqttConfig.deviceReadTopic().replace("+", daemonId) + "/$action"

        return publish(topic, message, qos)
    }

    // Further research on this:
    // There is a possibility that these calls can happen without
    // the application being loaded first, which can lead to publishes
    // being lost. This needs to be checked at some point.
    // So far it seems that it's not the case, but just to be sure.
    private fun publish(
        topic: String,
        message: String,
        qos: MqttQos
    ): Flowable<Mqtt5PublishResult>? {
        return mqttClient?.publish(
            Flowable.just(Mqtt5Publish.builder()
                .topic(topic)
                .qos(qos)
                .payload(message.toByteArray())
                .build()))
            ?.doOnComplete { Log.info("[client->mqtt] Successfully published <$message> to <$topic>!") }
    }

    /**
     * Rebuilds (replaces) current singleton client.
     */
    fun rebuildMqttClient(): Mqtt5RxClient {
        shutdownClient().blockingAwait() // Kill current client (null checks inside)

        // ---- Build Client ----
        val client = Mqtt5Client.builder()
            .identifier(
                "${mqttConfig.clientId()}-$mqttClientId".apply {
                    Log.info("[client->mqtt->reaper] // Building new MQTT client: $this")
                }
            )
            .serverHost(mqttConfig.host())
            .serverPort(mqttConfig.port())
            .also {
                it.simpleAuth(
                    Mqtt5SimpleAuth.builder()
                        .username(jwtUsername!!)
                        .build()
                )
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
                // Need to do this because RxJava's
                // error handling mechanism is futile
                // Project Reactor >>>>
                .flatMap { mqttClient!!.applySubscription() }
                .`as` { Flux.from(it.toFlowable(BackpressureStrategy.BUFFER)) }
                .retryWhen(
                    Retry.fixedDelay(Long.MAX_VALUE, Duration.ofSeconds(5))
                        .doBeforeRetry {
                            Log.info("[client->mqtt] // " +
                                    "Connecting to... ${mqttConfig.host()}:${mqttConfig.port()}")
                        }
                )
                .subscribe()
        }
    }

    /**
     * Define connection rules for MQTT Client building above.
     */
    private fun Mqtt5RxClient.connectScenario(): Observable<Mqtt5ConnAck> {
        return this.connectWith()
            // Clean session is required:
            // https://www.emqx.io/docs/en/latest/messaging/mqtt-shared-subscription.html#shared-subscription-and-session
            .cleanStart(true)
            .applyConnect()
            .doOnSubscribe { Log.info("[client->mqtt] // " +
                    "Connecting to... ${mqttConfig.host()}:${mqttConfig.port()}") }
            .doOnSuccess { Log.info("[client->mqtt] // " +
                    "Connected to ${mqttConfig.host()}:${mqttConfig.port()}, ${it.reasonCode}") }
            .doOnError { Log.error("[client->mqtt] // " +
                    "Connection failed to ${mqttConfig.host()}:${mqttConfig.port()}, ${it.message}") }
            .onErrorResumeNext {
                // if client is already connected, continue with a single that never
                // emits to stop reconnections
                if (mqttClient?.state?.isConnected == true)
                    return@onErrorResumeNext Single.never<Mqtt5ConnAck>()

                Single.error(it)
            }
            .toObservable()
    }

    /**
     * Subscribe to the given topics.
     */
    private fun Mqtt5RxClient.applySubscription(): Observable<Mqtt5Publish> {
        return this.subscribePublishesWith()
            .addSubscriptions(
                mqttConfig.getSubscriptions()
                    .map {
                        Mqtt5Subscription.builder()
                            .topicFilter(it)
                            .qos(MqttQos.AT_LEAST_ONCE)
                            .build()
                    })
            .applySubscribe()
            .doOnSubscribe {
                Log.info("[client->mqtt] // " + "Subscribing to topics [" +
                        "${mqttConfig.getSubscriptions().joinToString(", ") { "'${it}'" }}]")
            }
            .doOnNext { mqttRouter.route(it) }
            .doOnError { mqttRouter.route(it) }
            .doOnTerminate {
                Log.info("[client->mqtt] // " +
                        "Connection to ${mqttConfig.host()}:${mqttConfig.port()} ended.")
            }
            .toObservable()
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