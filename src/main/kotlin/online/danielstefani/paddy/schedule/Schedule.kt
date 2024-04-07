package online.danielstefani.paddy.schedule

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.validation.constraints.NotNull
import online.danielstefani.paddy.daemon.Daemon
import online.danielstefani.paddy.schedule.validation.Timezone
import online.danielstefani.paddy.schedule.validation.UnixCron
import online.danielstefani.paddy.schedule.validation.ValidSchedule
import org.neo4j.ogm.annotation.GeneratedValue
import org.neo4j.ogm.annotation.Id
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Relationship
import java.util.*

@NodeEntity
@ValidSchedule
open class Schedule {

    @Id
    @GeneratedValue
    var id: Long? = null

    // What the action will do: ON, OFF etc.
    @NotNull
    var type: ScheduleType? = null

    @Timezone
    var timezone = TimeZone.getTimeZone("UTC").toZoneId().id

    // Signifies that this Schedule will run only once.
    // Execution time in UNIX seconds
    var single: Long? = null

    // Signifies that this Schedule will run periodically.
    @UnixCron
    var periodic: String? = null

    // The next time this Schedule will be executed
    var nextExecution: Long? = null

    // Signifies that this Schedule will finish running at some point.
    // If null, this Schedule runs indefinitely.
    var finish: Long? = null

    @JsonIgnore
    @Relationship(type = "IS_SCHEDULED", direction = Relationship.Direction.INCOMING)
    var daemon: Daemon? = null

    fun isPeriodic(): Boolean {
        return periodic != null
    }
}