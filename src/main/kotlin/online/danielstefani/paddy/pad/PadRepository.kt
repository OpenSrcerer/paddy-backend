package online.danielstefani.paddy.pad

import jakarta.enterprise.context.ApplicationScoped
import online.danielstefani.paddy.repository.AbstractNeo4jRepository
import online.danielstefani.paddy.user.User
import online.danielstefani.paddy.util.get
import org.neo4j.ogm.session.queryForObject

@ApplicationScoped
class PadRepository : AbstractNeo4jRepository() {

    fun get(id: String): Pad? {
        return with(neo4j.openSession()) {
            val existingPad = this.queryForObject<Pad>(
                """
                    MATCH (node:Pad)
                    WHERE ID(node) = $id
                    RETURN node
                """, emptyMap())

            if (existingPad != null) existingPad
            else null
        }
    }



    fun getUserPad(user: User, id: String): Pad? {
        return with(neo4j.openSession()) {
            this.queryForObject<Pad>(
                """
                    MATCH (ux:User { username: "${user.username}" })
                        -[:OWNS]-> (px:Pad)
                    WHERE ID(px) = $id
                    RETURN px
                """, emptyMap<String, String>())
        }
    }


    fun getAllUserPads(user: User): List<Pad> {
        return with(neo4j.openSession()) {
            val result = this.query(
                """
                    MATCH (ux:User { username: "${user.username}" })
                        -[:OWNS]-> (px:Pad)
                    RETURN px
                """, emptyMap<String, String>())

            result.get()
        }
    }

    fun createUserPad(user: User): Pad {
        return with(neo4j.openSession()) {
            Pad().also {
                it.user = user

                this.save(it)
            }
        }
    }

    fun deleteUserPad(user: User, id: String): Pad? {
        return with(neo4j.openSession()) {
            val existingPad = getUserPad(user, id)

            if (existingPad == null) null
            else existingPad.also { this.delete(it) }
        }
    }
}