package online.danielstefani.paddy.schedule.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = [TimezoneValidator::class])
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Timezone(
    val message: String = "Field must be valid timezone\nhttps://en.wikipedia.org/wiki/List_of_tz_database_time_zones",

    val groups: Array<KClass<*>> = [],

    val payload: Array<KClass<out Payload>> = []
)
