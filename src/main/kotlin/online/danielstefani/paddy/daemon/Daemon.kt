package online.danielstefani.paddy.daemon

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import online.danielstefani.paddy.power.Power
import online.danielstefani.paddy.schedule.Schedule
import online.danielstefani.paddy.user.User
import org.neo4j.ogm.annotation.Id
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Relationship

@NodeEntity
open class Daemon() {

    // Copy constructor
    constructor(daemon: Daemon) : this() {
        this.id = daemon.id
        this.user = daemon.user
        this.lastRssi = daemon.lastRssi
        this.lastPing = daemon.lastPing
    }

    @Id @NotNull @Pattern(regexp = "\\d+")
    var id: String? = null

    var on: Boolean = false

    // Wi-Fi Signal Strength
    var lastRssi: Int? = null

    var lastPing: Long = 0

    var recovery: Boolean = false

    @JsonIgnore
    @Relationship(type = "OWNS", direction = Relationship.Direction.INCOMING)
    var user: User? = null

    @JsonIgnore
    @Relationship(type = "IS_SCHEDULED", direction = Relationship.Direction.OUTGOING)
    var schedules = setOf<Schedule>()

    @JsonIgnore
    @Relationship(type = "DRAWS", direction = Relationship.Direction.OUTGOING)
    var powers = setOf<Power>()
}