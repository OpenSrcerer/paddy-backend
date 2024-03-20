package online.danielstefani.paddy.schedule

import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped
import online.danielstefani.paddy.daemon.Daemon
import online.danielstefani.paddy.repository.AbstractNeo4jRepository
import online.danielstefani.paddy.repository.RequestScopedNeo4jSession
import online.danielstefani.paddy.util.get
import org.neo4j.ogm.session.queryForObject

@ApplicationScoped
class ScheduleRepository(
    private val session: RequestScopedNeo4jSession
) : AbstractNeo4jRepository() {

    fun get(id: Long, daemonId: String? = null): Schedule? {
        val queryBuilder = StringBuilder().append("MATCH ")

        if (daemonId != null) {
            queryBuilder.append("(dx:Daemon { id: \"$daemonId\" })-[:IS_SCHEDULED]->")
        }

        val query = queryBuilder.append("(sx:Schedule) WHERE ID(sx) = $id RETURN sx").toString()

        return session().queryForObject<Schedule>(query, emptyMap())
    }

    fun getAll(daemonId: String, username: String): List<Schedule> {
        val query = """
                    MATCH 
                    (ux:User { username: "$username" })
                        -[:OWNS]->
                    (dx:Daemon { id: "$daemonId" })
                        -[:IS_SCHEDULED]->
                    (sx:Schedule)
                    RETURN sx
                """

        return session().query(query, emptyMap<String, String>()).get()
    }

    fun update(
        id: Long,
        daemonId: String? = null,
        updater: (Schedule) -> Unit
    ): Schedule? {
        return get(id, daemonId)?.also {
            updater.invoke(it)

            session().save(it)
        }
    }

    fun create(
        daemon: Daemon,
        schedule: Schedule
    ): Schedule {
        return schedule.also {
            it.daemon = daemon

            session().save(it)
        }
    }

    fun delete(id: Long, daemonId: String? = null): Schedule? {
        return get(id, daemonId)
            ?.also {
                Log.info(it)
                session().delete(it)
            }
    }
}