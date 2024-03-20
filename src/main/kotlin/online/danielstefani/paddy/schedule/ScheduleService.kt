package online.danielstefani.paddy.schedule

import jakarta.enterprise.context.ApplicationScoped
import online.danielstefani.paddy.daemon.DaemonService

@ApplicationScoped
class ScheduleService(
    private val scheduleRepository: ScheduleRepository,
    private val daemonService: DaemonService
) {
    fun createSchedule(
        daemonId: String,
        schedule: Schedule
    ): Schedule? {
        val daemon = daemonService.getDaemon(daemonId) ?: return null
        return scheduleRepository.create(daemon, schedule)
    }
}