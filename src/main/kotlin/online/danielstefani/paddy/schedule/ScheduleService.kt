package online.danielstefani.paddy.schedule

import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.model.time.ExecutionTime
import com.cronutils.parser.CronParser
import jakarta.enterprise.context.ApplicationScoped
import online.danielstefani.paddy.daemon.DaemonService
import java.time.ZoneId
import java.time.ZonedDateTime

@ApplicationScoped
class ScheduleService(
    private val scheduleRepository: ScheduleRepository,
    private val daemonService: DaemonService
) {
    companion object {
        private val cronParser = CronParser(
            CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX))
    }

    fun createSchedule(
        daemonId: String,
        schedule: Schedule
    ): Schedule? {
        val daemon = daemonService.getDaemon(daemonId) ?: return null

        if (schedule.isPeriodic()) {
            schedule.nextExecution = ExecutionTime.forCron(cronParser.parse(schedule.periodic))
                .nextExecution(ZonedDateTime.now(ZoneId.of(schedule.timezone)))
                .map { it.toEpochSecond() }
                .orElseThrow {
                    IllegalArgumentException("Cron execution time could not be generated for cron: <${schedule.periodic}>!") }
        }

        return scheduleRepository.create(daemon, schedule)
    }
}