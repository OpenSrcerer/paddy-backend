package online.danielstefani.paddy.user

import online.danielstefani.paddy.pad.Pad
import org.neo4j.ogm.annotation.Id
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Relationship
import java.util.UUID

@NodeEntity
class User {
    @Id
    var id: String? = null

    var email: String? = null

    var username: String? = null

    var passwordHash: String? = null // PBKDF2 hash

    var passwordSalt: String? = null // PBKDF2 salt

    @Relationship(type = "OWNS", direction = Relationship.Direction.OUTGOING)
    var pads = setOf<Pad>()
}