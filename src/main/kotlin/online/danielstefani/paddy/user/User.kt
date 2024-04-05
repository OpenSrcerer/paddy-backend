package online.danielstefani.paddy.user

import com.fasterxml.jackson.annotation.JsonIgnore
import online.danielstefani.paddy.daemon.Daemon
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

    /*
    The refresh token serial is a simple mechanism.
    All refresh tokens are signed with this identifier.

    If this identifier differs from the token, the refresh token is invalid.
     */
    @JsonIgnore
    var refreshTokenSerial: String? = null

    @Relationship(type = "OWNS", direction = Relationship.Direction.OUTGOING)
    var daemons = setOf<Daemon>()
}