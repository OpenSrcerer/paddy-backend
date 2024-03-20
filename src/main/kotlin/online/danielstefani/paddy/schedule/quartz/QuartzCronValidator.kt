package online.danielstefani.paddy.schedule.quartz

import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.parser.CronParser
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class QuartzCronValidator : ConstraintValidator<QuartzCron, String> {
    override fun isValid(string: String?, ctx: ConstraintValidatorContext?): Boolean {
        if (string == null) return false

        try {
            CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ))
                .parse(string)
            return true
        } catch (ex: IllegalArgumentException) {
            return false
        }
    }
}