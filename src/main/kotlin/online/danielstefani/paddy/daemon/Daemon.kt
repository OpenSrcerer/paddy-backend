package online.danielstefani.paddy.daemon

import com.fasterxml.jackson.annotation.JsonIgnore
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
        this.lastPing = daemon.lastPing
    }

    @Id
    var id: String? = null

    var on: Boolean = false

    var lastPing: Long = 0

    @JsonIgnore
    @Relationship(type = "OWNS", direction = Relationship.Direction.INCOMING)
    var user: User? = null
}