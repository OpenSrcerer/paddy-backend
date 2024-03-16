package online.danielstefani.paddy.daemon

import jakarta.enterprise.context.ApplicationScoped
import online.danielstefani.paddy.repository.AbstractNeo4jRepository
import online.danielstefani.paddy.user.User
import online.danielstefani.paddy.util.get
import org.neo4j.ogm.session.queryForObject

@ApplicationScoped
class DaemonRepository : AbstractNeo4jRepository() {

    fun get(id: String): Daemon? {
        return with(neo4j.openSession()) {
            this.queryForObject<Daemon>(
                """
                    MATCH (node:Daemon { id: "$id" })
                    RETURN node
                """, emptyMap())
        }
    }



    fun getUserDaemon(user: User, id: String): Daemon? {
        return with(neo4j.openSession()) {
            this.queryForObject<Daemon>(
                """
                    MATCH (ux:User { username: "${user.username}" })
                        -[:OWNS]-> (dx:Daemon)
                    WHERE dx.id = "$id"
                    RETURN dx
                """, emptyMap<String, String>())
        }
    }


    fun getAllUserDaemons(user: User): List<Daemon> {
        return with(neo4j.openSession()) {
            val result = this.query(
                """
                    MATCH (ux:User { username: "${user.username}" })
                        -[:OWNS]-> (dx:Daemon)
                    RETURN dx
                """, emptyMap<String, String>())

            result.get()
        }
    }

    fun createUserDaemon(user: User, id: String): Daemon? {
        val daemon = get(id)
        if (daemon != null) return null

        return with(neo4j.openSession()) {
            Daemon().also {
                it.id = id
                it.user = user

                this.save(it)
            }
        }
    }

    fun deleteUserDaemon(user: User, id: String): Daemon? {
        return with(neo4j.openSession()) {
            getUserDaemon(user, id)?.also { this.delete(it) }
        }
    }
}