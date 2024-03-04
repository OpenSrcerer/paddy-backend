package online.danielstefani.paddy.pad

import jakarta.enterprise.context.ApplicationScoped
import online.danielstefani.paddy.repository.AbstractNeo4jRepository
import online.danielstefani.paddy.user.User
import online.danielstefani.paddy.util.get
import org.neo4j.ogm.session.queryForObject

@ApplicationScoped
class DaemonRepository : AbstractNeo4jRepository() {

    fun get(id: String): Daemon? {
        return with(neo4j.openSession()) {
            val existingDaemon = this.queryForObject<Daemon>(
                """
                    MATCH (node:Daemon)
                    WHERE ID(node) = $id
                    RETURN node
                """, emptyMap())

            if (existingDaemon != null) existingDaemon
            else null
        }
    }



    fun getUserDaemon(user: User, id: String): Daemon? {
        return with(neo4j.openSession()) {
            this.queryForObject<Daemon>(
                """
                    MATCH (ux:User { username: "${user.username}" })
                        -[:OWNS]-> (px:Daemon)
                    WHERE ID(px) = $id
                    RETURN px
                """, emptyMap<String, String>())
        }
    }


    fun getAllUserDaemons(user: User): List<Daemon> {
        return with(neo4j.openSession()) {
            val result = this.query(
                """
                    MATCH (ux:User { username: "${user.username}" })
                        -[:OWNS]-> (px:Daemon)
                    RETURN px
                """, emptyMap<String, String>())

            result.get()
        }
    }

    fun createUserDaemon(user: User): Daemon {
        return with(neo4j.openSession()) {
            Daemon().also {
                it.user = user

                this.save(it)
            }
        }
    }

    fun deleteUserDaemon(user: User, id: String): Daemon? {
        return with(neo4j.openSession()) {
            val existingPad = getUserDaemon(user, id)

            if (existingPad == null) null
            else existingPad.also { this.delete(it) }
        }
    }
}