package online.danielstefani.paddy.schedule.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import online.danielstefani.paddy.schedule.Schedule

class ValidScheduleValidator : ConstraintValidator<ValidSchedule, Schedule> {

    override fun isValid(schedule: Schedule?, ctx: ConstraintValidatorContext?): Boolean {
        if (schedule == null) return false
        if (schedule.single == null && schedule.periodic == null) return false

        return true
    }
}