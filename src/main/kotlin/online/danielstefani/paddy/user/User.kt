package online.danielstefani.paddy.user

import com.fasterxml.jackson.annotation.JsonIgnore
import online.danielstefani.paddy.pad.Daemon
import org.neo4j.ogm.annotation.Id
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Relationship

@NodeEntity
class User {
    @Id
    var id: String? = null

    var email: String? = null

    var username: String? = null

    @JsonIgnore
    var passwordHash: String? = null // PBKDF2 hash

    @JsonIgnore
    var passwordSalt: String? = null // PBKDF2 salt

    @Relationship(type = "OWNS", direction = Relationship.Direction.OUTGOING)
    var daemons = setOf<Daemon>()
}