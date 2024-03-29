package online.danielstefani.paddy.power

import com.fasterxml.jackson.annotation.JsonIgnore
import online.danielstefani.paddy.daemon.Daemon
import org.neo4j.ogm.annotation.GeneratedValue
import org.neo4j.ogm.annotation.Id
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Relationship

@NodeEntity
open class Power {

    @Id
    @GeneratedValue
    @JsonIgnore
    var id: Long? = null

    var w: Float? = null

    var timestamp: Long? = null

    @JsonIgnore
    @Relationship(type = "DRAWS", direction = Relationship.Direction.INCOMING)
    var daemon: Daemon? = null

}