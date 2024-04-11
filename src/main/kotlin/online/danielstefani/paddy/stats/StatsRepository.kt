package online.danielstefani.paddy.stats

import jakarta.enterprise.context.ApplicationScoped
import online.danielstefani.paddy.repository.AbstractNeo4jRepository
import online.danielstefani.paddy.stats.dto.PowerStatistic
import online.danielstefani.paddy.stats.dto.PowerTemporal

@ApplicationScoped
class StatsRepository : AbstractNeo4jRepository() {

    fun getCumulativePowerUsage(
        daemonId: String,
        temporal: PowerTemporal,
        limit: Int? = 10,
        before: Long? = null,
        after: Long? = null
    ): List<PowerStatistic> {
        val replacement =
            if (before != null && after != null) "WHERE px.timestamp > $after AND px.timestamp < $before"
            else if (after != null) "WHERE px.timestamp > $after"
            else if (before != null) "WHERE px.timestamp < $before"
            else ""

        val query = """
                    // ---- Step 1: Get all power measurements ----
                    MATCH (dx:Daemon { id: "$daemonId" })-[:DRAWS]->(px:Power)
                    ?1
                    WITH px
                    ORDER BY px.timestamp
                    
                    // ---- Step 2: Order all power measurements in an iterator to calculate delta time ----
                    WITH COLLECT(px) AS powerReadings
                    UNWIND range(0, size(powerReadings) - 2) AS i
                    WITH powerReadings, powerReadings[i] AS currentPower, powerReadings[i + 1] AS nextPower
                    ORDER BY currentPower.timestamp
                    
                    // ---- Step 3: Get delta time ----
                    WITH 
                        powerReadings,
                        currentPower, 
                        nextPower,
                        nextPower.timestamp - currentPower.timestamp AS timeDeltaSeconds,
                        currentPower.w AS w
                    WHERE (timeDeltaSeconds < 100)
                        
                    // ---- Step 4: Calculate power usage in Watt-Seconds ----
                    WITH 
                        powerReadings, 
                        nextPower.timestamp / $temporal AS time_cursor,
                        timeDeltaSeconds * w AS Ws
                    
                    // ---- Final Step: Return Watt-Hours aggregated by temporal ----
                    WITH 
                        time_cursor * $temporal AS temporal,
                        sum(Ws / 3600) AS statistic,
                        powerReadings[0].timestamp AS eldestTimestamp
                    RETURN CASE
                        WHEN temporal < eldestTimestamp THEN eldestTimestamp 
                        ELSE temporal 
                    END AS temporal, statistic
                    ORDER BY temporal DESC
                    ?2
                """
            .replace("?1", replacement)
            .replace("?2", if (limit == null) "" else "LIMIT $limit")

        return session.query<PowerStatistic>(query).reversed()
    }

    fun getAveragePowerUsage(
        daemonId: String,
        temporal: PowerTemporal,
        limit: Int? = 10,
        before: Long? = null,
        after: Long? = null
    ): List<PowerStatistic> {
        val replacement =
            if (before != null && after != null) "WHERE px.timestamp > $after AND px.timestamp < $before"
            else if (after != null) "WHERE px.timestamp > $after"
            else if (before != null) "WHERE px.timestamp < $before"
            else ""

        val query = """
                    MATCH (dx:Daemon { id: "$daemonId" }) -[:DRAWS]-> (px:Power)
                    ?1
                    WITH px.timestamp / $temporal AS time_cursor, px.w AS w
                    RETURN time_cursor * $temporal AS temporal, avg(w) AS statistic
                    ORDER BY temporal DESC
                    ?2
                """
            .replace("?1", replacement)
            .replace("?2", if (limit == null) "" else "LIMIT $limit")

        return session.query<PowerStatistic>(query).reversed()
    }

}