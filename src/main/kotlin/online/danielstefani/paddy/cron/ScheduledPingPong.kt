package online.danielstefani.paddy.cron

import io.quarkus.scheduler.Scheduled
import jakarta.enterprise.context.ApplicationScoped
import online.danielstefani.paddy.client.RxMqttClient

@ApplicationScoped
class ScheduledPingPong(
    private val rxMqttClient: RxMqttClient
) {

    @Scheduled(every = "30s")
    fun pingDevicesPeriodically() {
        rxMqttClient.publish("device-multicast", "{\"message\":\"Ping!\"}")
            ?.subscribe()
    }
}