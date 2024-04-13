package online.danielstefani.paddy.mqtt

import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class DaemonAction(
    val action: String = "",
)
