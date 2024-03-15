package online.danielstefani.paddy.daemon

import com.fasterxml.jackson.annotation.JsonIgnore
import online.danielstefani.paddy.user.User
import org.neo4j.ogm.annotation.Id
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Relationship

@NodeEntity
class Daemon {
    @Id
    var id: Long? = null

    @JsonIgnore
    @Relationship(type = "OWNS", direction = Relationship.Direction.INCOMING)
    var user: User? = null
}