package online.danielstefani.paddy.stats

import io.quarkus.security.Authenticated
import io.smallrye.mutiny.Uni
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import online.danielstefani.paddy.stats.dto.AveragePowerEveryTemporal
import online.danielstefani.paddy.stats.dto.PowerTemporal
import online.danielstefani.paddy.stats.dto.TotalPower
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
    ): Uni<List<AveragePowerEveryTemporal>> {
        return Uni.createFrom().emitter {
            it.complete(statsRepository.getAveragePowerEveryTemporal(
                daemonId, temporal, limit, before, after))
        }
    }

    @GET
    @Path("/total")
    fun getTotalPower(
        @RestPath daemonId: String
    ): Uni<TotalPower> {
        return statsService.getTotalPower(daemonId)
    }

}