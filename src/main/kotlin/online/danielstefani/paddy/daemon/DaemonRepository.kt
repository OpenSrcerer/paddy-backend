package online.danielstefani.paddy.daemon

import jakarta.enterprise.context.ApplicationScoped
import online.danielstefani.paddy.repository.AbstractNeo4jRepository
import online.danielstefani.paddy.user.User

@ApplicationScoped
class DaemonRepository : AbstractNeo4jRepository() {

    fun get(id: String, username: String? = null): Daemon? {
        val query = """
                    MATCH (node:Daemon { id: "$id" })
                    RETURN node
                """

        return session.queryForObject<Daemon>(query)
    }

    fun update(
        id: String,
        updater: (Daemon) -> Unit
    ): Daemon? {
        return get(id)?.also {
            updater.invoke(it)

            session().save(it)
        }
    }

    fun getAllUserDaemons(user: User): List<Daemon> {
        return session.query<Daemon>(
                    """
                        MATCH (ux:User { username: "${user.username}" })
                            -[:OWNS]-> (dx:Daemon)
                        RETURN dx
                    """)
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

    fun deleteUserDaemon(id: String): Daemon? {
        return get(id)
            ?.also { session().delete(it) }
    }
}