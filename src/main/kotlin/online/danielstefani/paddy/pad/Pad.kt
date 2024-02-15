package online.danielstefani.paddy.pad

import org.neo4j.ogm.annotation.Id
import org.neo4j.ogm.annotation.NodeEntity

@NodeEntity
class Pad(
    @Id val serial: String,
    var jwt: String
)