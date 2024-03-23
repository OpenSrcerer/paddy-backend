package online.danielstefani.paddy.schedule.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = [QuartzCronValidator::class])
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class QuartzCron(
    val message: String = "Field must be valid Quartz Cron expression",

    val groups: Array<KClass<*>> = [],

    val payload: Array<KClass<out Payload>> = []
)
