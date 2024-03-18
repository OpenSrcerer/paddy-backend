package online.danielstefani.paddy.daemon

import jakarta.enterprise.context.ApplicationScoped
import online.danielstefani.paddy.repository.AbstractNeo4jRepository
import online.danielstefani.paddy.repository.RequestScopedNeo4jSession
import online.danielstefani.paddy.user.User
import online.danielstefani.paddy.util.get
import org.neo4j.ogm.session.queryForObject

@ApplicationScoped
class DaemonRepository(
    private val session: RequestScopedNeo4jSession
) : AbstractNeo4jRepository() {

    fun get(id: String, user: User? = null): Daemon? {
        val query = if (user == null)
            """
                    MATCH (node:Daemon { id: "$id" })
                    RETURN node
                """
        else
            """
                    MATCH (ux:User { username: "${user.username}" })
                        -[:OWNS]-> (dx:Daemon)
                    WHERE dx.id = "$id"
                    RETURN dx
                """

        return session().queryForObject<Daemon>(query, emptyMap())
    }

    fun update(
        id: String,
        user: User? = null,
        updater: (Daemon) -> Unit
    ): Daemon? {
        return get(id, user)?.also {
            updater.invoke(it)

            session().save(it)
        }
    }

    fun getAllUserDaemons(user: User): List<Daemon> {
        val result = session().query(
            """
                    MATCH (ux:User { username: "${user.username}" })
                        -[:OWNS]-> (dx:Daemon)
                    RETURN dx
                """, emptyMap<String, String>())

        return result.get()
    }

    fun createUserDaemon(id: String, user: User): Daemon? {
        val daemon = get(id)
        if (daemon != null) return null

        return Daemon().also {
            it.id = id
            it.user = user

            session().save(it)
        }
    }

    fun deleteUserDaemon(id: String, user: User): Daemon? {
        return get(id, user)
            ?.also { session().delete(it) }
    }
}