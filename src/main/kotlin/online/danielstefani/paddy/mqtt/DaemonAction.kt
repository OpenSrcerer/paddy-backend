package online.danielstefani.paddy.mqtt

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class DaemonAction(
    val action: String = "",
)
