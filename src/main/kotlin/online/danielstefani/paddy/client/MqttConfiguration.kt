package online.danielstefani.paddy.client

import io.smallrye.config.ConfigMapping
import io.smallrye.config.WithDefault

@ConfigMapping(prefix = "mqtt")
interface MqttConfiguration {

    @WithDefault(" ")
    fun username(): String

    @WithDefault(" ")
    fun password(): String

    @WithDefault("localhost")
    fun host(): String

    @WithDefault("1883")
    fun port(): Int

    @WithDefault("paddy-backend")
    fun clientId(): String

    fun subscriptions(): String

    fun getSubscriptions(): List<String> {
        if (subscriptions().isEmpty())
            throw IllegalArgumentException("No subscriptions provided!")

        return subscriptions().split(",")
    }
}