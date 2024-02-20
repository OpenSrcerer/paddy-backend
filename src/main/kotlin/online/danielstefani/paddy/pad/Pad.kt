package online.danielstefani.paddy.pad

import com.fasterxml.jackson.annotation.JsonIgnore
import online.danielstefani.paddy.user.User
import org.neo4j.ogm.annotation.Id
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Relationship

@NodeEntity
data class Pad(
    @Id
    val serial: String,

    var jwt: String,

    @JsonIgnore
    @Relationship(type = "OWNS", direction = Relationship.Direction.INCOMING)
    val user: User
)