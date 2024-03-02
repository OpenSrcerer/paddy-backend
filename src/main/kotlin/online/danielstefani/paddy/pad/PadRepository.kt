package online.danielstefani.paddy.pad

import jakarta.enterprise.context.ApplicationScoped
import online.danielstefani.paddy.repository.AbstractNeo4jRepository
import online.danielstefani.paddy.user.User
import online.danielstefani.paddy.util.get
import org.neo4j.ogm.session.queryForObject

@ApplicationScoped
class PadRepository : AbstractNeo4jRepository() {

    fun get(id: String): Pad? {
        with(neo4j.openSession()) {
            val existingPad = this.queryForObject<Pad>(
                """
                    MATCH (node:Pad)
                    WHERE node.id = "$id" 
                    RETURN node
                """, emptyMap())

            return if (existingPad != null) existingPad else null
        }
    }

    fun getAllUserPads(user: User): List<Pad> {
        with(neo4j.openSession()) {
            val result = this.query(
                """
                    MATCH (ux:User { username: "${user.username}" }) -[:OWNS]-> (px:Pad)
                    RETURN px
                """, emptyMap<String, String>())

            return result.get()
        }
    }

    fun create(user: User): Pad {
        with(neo4j.openSession()) {
            return Pad()
                .also {
                    it.user = user

                    this.save(it)
                }
        }
    }
}