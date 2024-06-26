package online.danielstefani.paddy.power

import jakarta.enterprise.context.ApplicationScoped
import online.danielstefani.paddy.daemon.Daemon
import online.danielstefani.paddy.repository.AbstractNeo4jRepository

@ApplicationScoped
class PowerRepository : AbstractNeo4jRepository() {

    fun get(id: Long, daemonId: String? = null): Power? {
        val queryBuilder = StringBuilder().append("MATCH ")

        if (daemonId != null) {
            queryBuilder.append("(dx:Daemon { id: \"$daemonId\" })-[:DRAWS]->")
        }

        val query = queryBuilder.append("(px:Power) WHERE ID(px) = $id RETURN px").toString()

        return session.queryForObject<Power>(query)
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
                    CALL apoc.periodic.iterate(
                        "MATCH
                            (dx:Daemon { id: \"$daemonId\" })
                                -[:DRAWS]->
                            (px:Power) RETURN id(px) AS id",
                        "MATCH (px) WHERE id(px) = id DETACH DELETE px",
                        { batchSize:10000 })
                    YIELD batches, total
                    RETURN batches, total
                """

        session.query<Unit>(query)
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

        return session.query(query)
    }
}