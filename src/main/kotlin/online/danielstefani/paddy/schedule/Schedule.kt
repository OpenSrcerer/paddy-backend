package online.danielstefani.paddy.schedule

import com.cronutils.model.CronType
import com.cronutils.validation.Cron
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.annotation.Nullable
import jakarta.validation.constraints.NotNull
import online.danielstefani.paddy.daemon.Daemon
import online.danielstefani.paddy.schedule.quartz.QuartzCron
import org.neo4j.ogm.annotation.GeneratedValue
import org.neo4j.ogm.annotation.Id
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Relationship
import java.util.*

@NodeEntity
open class Schedule {

    @Id
    @GeneratedValue
    var id: Long? = null

    // What the action will do: ON, OFF etc.
    @NotNull
    var type: ScheduleType? = null

    var timezone = TimeZone.getTimeZone("UTC").toZoneId().id

    // Signifies that this Schedule will run only once.
    var single: Long? = null

    // Signifies that this Schedule will run periodically.
    @QuartzCron
    var periodic: String? = null

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