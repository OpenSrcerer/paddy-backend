package online.danielstefani.paddy.session

import io.smallrye.config.ConfigMapping
import io.smallrye.config.WithDefault

@ConfigMapping(prefix = "jwtcookie")
interface JwtCookieConfig {

    @WithDefault("paddy-jwt")
    fun name(): String

    @WithDefault("/")
    fun path(): String

    @WithDefault(".danielstefani.online")
    fun domain(): String
}