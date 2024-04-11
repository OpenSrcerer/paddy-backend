package online.danielstefani.paddy.stats

import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import online.danielstefani.paddy.stats.dto.PowerStatistic
import online.danielstefani.paddy.stats.dto.PowerTemporal

@ApplicationScoped
class StatsService(
    private val statsRepository: StatsRepository
) {

    /*
    This data is crunched with the average power measured every hour.
    The before & after limiters can be used to find out how much power
    the device has measured relative to two timestamps (ex. for 1 month, 1 year)
     */
    fun getTotalPower(
        daemonId: String,
        before: Long? = null,
        after: Long? = null
    ): Uni<PowerStatistic> {
        return Uni.createFrom().emitter { emitter ->
            val totalKwh = statsRepository.getCumulativePowerUsage(
                daemonId, PowerTemporal.YEAR, null, before = before, after = after)
                // The query here already returns power in Watt-Hours per year
                // Accumulate every year
                .fold(0.0) { acc, pwr -> acc + pwr.statistic!! }
                // Turn all measurements to kWh
                .div(1000)

            emitter.complete(PowerStatistic().also { it.statistic = totalKwh })
        }
    }

    fun getRollingPower(
        daemonId: String,
        temporal: PowerTemporal,
        limit: Int? = 10,
        before: Long? = null,
        after: Long? = null
    ): Uni<List<PowerStatistic>> {
        return Uni.createFrom().emitter {

            val temporalRollingKwh = statsRepository.getCumulativePowerUsage(
                daemonId, temporal, limit, before = before, after = after)
                // Turn all measurements to kWh
                .onEach { pwr -> pwr.statistic = pwr.statistic?.div(1000) }

            it.complete(temporalRollingKwh)
        }
    }
}