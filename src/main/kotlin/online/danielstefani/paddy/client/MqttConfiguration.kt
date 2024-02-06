package online.danielstefani.paddy.client

import io.smallrye.config.ConfigMapping
import io.smallrye.config.WithDefault

@ConfigMapping(prefix = "mqtt")
interface MqttConfiguration {

    @WithDefault("localhost")
    fun host(): String

    @WithDefault("1883")
    fun port(): Int

    @WithDefault("paddy-backend")
    fun clientId(): String

    // ---- Meta ---

    fun subscriptions(): String

    fun getSubscriptions(): List<String> {
        if (subscriptions().isEmpty())
            throw IllegalArgumentException("No subscriptions provided!")

        return subscriptions().split(",")
    }
}