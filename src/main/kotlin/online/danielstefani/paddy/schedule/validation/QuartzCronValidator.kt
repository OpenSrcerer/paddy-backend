package online.danielstefani.paddy.schedule.validation

import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.parser.CronParser
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class QuartzCronValidator : ConstraintValidator<QuartzCron, String> {
    override fun isValid(string: String?, ctx: ConstraintValidatorContext?): Boolean {
        // This is allowed because the "single"
        // field may be populated
        if (string == null) return true

        try {
            CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ))
                .parse(string)
            return true
        } catch (ex: IllegalArgumentException) {
            return false
        }
    }
}