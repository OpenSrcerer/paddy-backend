package online.danielstefani.paddy.schedule

import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.model.time.ExecutionTime
import com.cronutils.parser.CronParser
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull
import online.danielstefani.paddy.daemon.Daemon
import online.danielstefani.paddy.schedule.validation.Timezone
import online.danielstefani.paddy.schedule.validation.UnixCron
import online.danielstefani.paddy.schedule.validation.ValidSchedule
import org.neo4j.ogm.annotation.GeneratedValue
import org.neo4j.ogm.annotation.Id
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Relationship
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

@NodeEntity
@ValidSchedule
open class Schedule {

    companion object {
        private val cronParser = CronParser(
            CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX))
    }

    @Id
    @GeneratedValue
    var id: Long? = null

    // What the action will do: ON, OFF etc.
    @NotNull
    var type: ScheduleType? = null

    @Timezone
    var timezone = TimeZone.getTimeZone("UTC").toZoneId().id

    // Signifies that this Schedule will run periodically.
    // Not populated for "single" schedules.
    @UnixCron
    var periodic: String? = null

    // The next time this Schedule will be executed.
    var nextExecution: Long? = null

    // Signifies that this Schedule will finish running at some point.
    // If null, this Schedule runs indefinitely.
    var finish: Long? = null

    @JsonIgnore
    @Relationship(type = "IS_SCHEDULED", direction = Relationship.Direction.INCOMING)
    var daemon: Daemon? = null

    @JsonProperty
    fun secondsUntil() = nextExecution!! - Instant.now().epochSecond

    @JsonProperty
    fun interval(): Long? = cronParser.parse(periodic)?.let { cron ->

        val exec = ExecutionTime.forCron(cron)
        val zone = ZonedDateTime.now(ZoneId.of(timezone))

        return exec.timeFromLastExecution(zone)
            .flatMap { last -> exec.timeToNextExecution(zone).map { next -> last + next } }
            .map { it.toSeconds() }
            .orElse(null)
    }

    fun isPeriodic() = periodic != null
}