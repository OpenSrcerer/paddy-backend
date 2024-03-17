package online.danielstefani.paddy.cron

import io.quarkus.scheduler.Scheduled
import jakarta.enterprise.context.ApplicationScoped
import online.danielstefani.paddy.mqtt.RxMqttClient

@ApplicationScoped
class ScheduledPingPong(
    private val rxMqttClient: RxMqttClient
) {

//    @Scheduled(every = "15s")
    fun pingDevicesPeriodically() {
//        rxMqttClient.publish("device-reads", "{\"message\":\"Ping!\"}")
//            ?.subscribe()
    }
}