package online.danielstefani.paddy.power

import jakarta.enterprise.context.ApplicationScoped
import online.danielstefani.paddy.daemon.Daemon
import online.danielstefani.paddy.repository.AbstractNeo4jRepository
import online.danielstefani.paddy.util.get
import org.neo4j.ogm.session.queryForObject

@ApplicationScoped
class PowerRepository : AbstractNeo4jRepository() {

    fun get(id: Long, daemonId: String? = null): Power? {
        val queryBuilder = StringBuilder().append("MATCH ")

        if (daemonId != null) {
            queryBuilder.append("(dx:Daemon { id: \"$daemonId\" })-[:DRAWS]->")
        }

        val query = queryBuilder.append("(px:Power) WHERE ID(px) = $id RETURN px").toString()

        return session().queryForObject<Power>(query, emptyMap())
    }

    fun getAll(daemonId: String, username: String): List<Power> {
        val query = """
                    MATCH 
                        (ux:User { username: "$username" })
                            -[:OWNS]->
                        (dx:Daemon { id: "$daemonId" })
                            -[:DRAWS]->
                        (px:Power)
                    RETURN px
                """

        return session().query(query, emptyMap<String, String>()).get()
    }

    /*
    Get all Power-s between two UNIX timestamps.
    Both the timestamps are exclusive.
    */
    fun getAllBetween(
        daemonId: String,
        username: String,
        limit: Int = 25,
        before: Long? = null,
        after: Long? = null
    ): List<Power> {
        val query = """
                    MATCH 
                        (ux:User { username: "$username" })
                            -[:OWNS]->
                        (dx:Daemon { id: "$daemonId" })
                            -[:DRAWS]->
                        (px:Power)
                        ?
                    RETURN px
                    LIMIT $limit
                """

        val replacement =
            if (before != null && after != null) "WHERE px.timestamp > $after AND px.timestamp < $before"
            else if (after != null) "WHERE px.timestamp > $after"
            else if (before != null) "WHERE px.timestamp < $before"
            else ""

        return session().query(query, mapOf("?" to replacement)).get()
    }

    fun create(
        daemon: Daemon,
        power: Power
    ): Power {
        return power.also {
            it.daemon = daemon

            session().save(it)
        }
    }

    fun deleteAll(daemonId: String) {
        val query = """
                    MATCH
                        (dx:Daemon { id: "$daemonId" })
                            -[:DRAWS]->
                        (px:Power)
                    DETACH DELETE px
                """

        session().query(query, emptyMap<String, String>())
    }

    fun deleteAllBefore(
        daemonId: String,
        after: Long
    ): List<Power> {
        val query = """
                    MATCH
                        (dx:Daemon { id: "$daemonId" })
                            -[:DRAWS]->
                        (px:Power)
                        WHERE px.w < $after
                    RETURN px
                """

        return session().query(query, emptyMap<String, String>()).get()
    }
}