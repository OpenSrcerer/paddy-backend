package online.danielstefani.paddy.schedule

import jakarta.enterprise.context.ApplicationScoped
import online.danielstefani.paddy.daemon.Daemon
import online.danielstefani.paddy.repository.AbstractNeo4jRepository

@ApplicationScoped
class ScheduleRepository : AbstractNeo4jRepository() {

    fun get(id: Long, daemonId: String? = null): Schedule? {
        val queryBuilder = StringBuilder().append("MATCH ")

        if (daemonId != null) {
            queryBuilder.append("(dx:Daemon { id: \"$daemonId\" })-[:IS_SCHEDULED]->")
        }

        val query = queryBuilder.append("(sx:Schedule) WHERE ID(sx) = $id RETURN sx").toString()

        return session.queryForObject<Schedule>(query)
    }

    fun getAll(daemonId: String): List<Schedule> {
        val query = """
                    MATCH 
                        (dx:Daemon { id: "$daemonId" })
                            -[:IS_SCHEDULED]->
                        (sx:Schedule)
                    RETURN sx
                """

        return session.query(query)
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
                session().delete(it)
            }
    }

    fun deleteAll(daemonId: String) {
        val query = """
                    MATCH
                        (dx:Daemon { id: "$daemonId" })
                            -[:IS_SCHEDULED]->
                        (sx:Schedule)
                    DETACH DELETE sx
                """

        session.query<Schedule>(query)
    }
}