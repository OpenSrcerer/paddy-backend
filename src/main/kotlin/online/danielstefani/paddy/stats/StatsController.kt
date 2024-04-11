package online.danielstefani.paddy.stats

import io.quarkus.security.Authenticated
import io.smallrye.mutiny.Uni
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import online.danielstefani.paddy.stats.dto.PowerStatistic
import online.danielstefani.paddy.stats.dto.PowerTemporal
import org.jboss.resteasy.reactive.RestPath
import org.jboss.resteasy.reactive.RestQuery

@Path("/daemon/{daemonId}/stats")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
class StatsController(
    private val statsRepository: StatsRepository,
    private val statsService: StatsService
) {

    @GET
    @Path("/average")
    fun getAveragePower(
        @RestPath daemonId: String,
        @RestQuery @DefaultValue("MINUTE") temporal: PowerTemporal,
        @RestQuery @DefaultValue("10") limit: Int,
        @RestQuery before: Long? = null,
        @RestQuery after: Long? = null
    ): Uni<List<PowerStatistic>> {
        return Uni.createFrom().emitter {
            it.complete(statsRepository.getAveragePowerUsage(
                daemonId, temporal, limit, before, after))
        }
    }

    @GET
    @Path("/total")
    fun getTotalPower(
        @RestPath daemonId: String,
        @RestQuery before: Long? = null,
        @RestQuery after: Long? = null
    ): Uni<PowerStatistic> {
        return statsService.getTotalPower(daemonId, before, after)
    }

    @GET
    @Path("/rolling")
    fun getRollingAverage(
        @RestPath daemonId: String,
        @RestQuery @DefaultValue("MINUTE") temporal: PowerTemporal,
        @RestQuery @DefaultValue("10") limit: Int,
        @RestQuery before: Long? = null,
        @RestQuery after: Long? = null
    ): Uni<List<PowerStatistic>> {
        return statsService.getRollingPower(daemonId, temporal, limit, before, after)
    }
}