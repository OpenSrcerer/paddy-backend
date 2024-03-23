package online.danielstefani.paddy.schedule.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = [ValidScheduleValidator::class])
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidSchedule(
    val message: String = "Schedule must include at least a single or periodic time",

    val groups: Array<KClass<*>> = [],

    val payload: Array<KClass<out Payload>> = []
)
