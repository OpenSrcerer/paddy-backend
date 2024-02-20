package online.danielstefani.paddy.user

import online.danielstefani.paddy.pad.Pad
import org.neo4j.ogm.annotation.Id
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Relationship
import java.util.UUID

@NodeEntity
data class User(
    @Id
    val id: UUID,

    val email: String,

    val username: String,

    val password: String, // SHA-256 hash of password

    @Relationship(type = "OWNS", direction = Relationship.Direction.OUTGOING)
    val pads: Set<Pad>
)