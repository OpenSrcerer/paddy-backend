package online.danielstefani.paddy.stats

import jakarta.enterprise.context.ApplicationScoped
import online.danielstefani.paddy.repository.AbstractNeo4jRepository
import online.danielstefani.paddy.stats.dto.AveragePower
import online.danielstefani.paddy.stats.dto.PowerTemporal

@ApplicationScoped
class StatsRepository : AbstractNeo4jRepository() {

    fun getAveragePowerEveryTemporal(
        daemonId: String,
        temporal: PowerTemporal,
        limit: Int? = 10,
        before: Long? = null,
        after: Long? = null
    ): List<AveragePower> {
        val replacement =
            if (before != null && after != null) "WHERE time_cursor > $after AND time_cursor < $before"
            else if (after != null) "WHERE time_cursor > $after"
            else if (before != null) "WHERE time_cursor < $before"
            else ""

        val query = """
                    MATCH (dx:Daemon { id: "$daemonId" }) -[:DRAWS]-> (px:Power)
                    ?1
                    WITH px.timestamp / $temporal AS time_cursor, px.w AS w
                    RETURN time_cursor * $temporal AS temporal, avg(w) AS averageW
                    ORDER BY temporal DESC
                    ?2
                """
            .replace("?1", replacement)
            .replace("?2", if (limit == null) "" else "LIMIT $limit")

        return session.query<AveragePower>(query)
    }

}