package online.danielstefani.paddy.schedule.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import java.time.ZoneId

class TimezoneValidator : ConstraintValidator<Timezone, String> {

    override fun isValid(string: String?, ctx: ConstraintValidatorContext?): Boolean {
        if (string == null) return false

        try {
            ZoneId.of(string)
            return true
        } catch (ex: Exception) {
            return false
        }
    }
}