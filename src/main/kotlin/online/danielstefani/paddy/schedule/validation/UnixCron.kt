package online.danielstefani.paddy.schedule.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = [UnixCronValidator::class])
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class UnixCron(
    val message: String = "Field must be valid Unix Cron expression",

    val groups: Array<KClass<*>> = [],

    val payload: Array<KClass<out Payload>> = []
)
