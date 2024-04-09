package online.danielstefani.paddy.stats

import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import online.danielstefani.paddy.stats.dto.PowerTemporal
import online.danielstefani.paddy.stats.dto.TotalPower

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
    ): Uni<TotalPower> {
        return Uni.createFrom().emitter {
            val totalKwh = statsRepository.getAveragePowerEveryTemporal(
                daemonId, PowerTemporal.HOUR, null, before = before, after = after)
                // Turn all measurements to kW
                .onEach { pwr -> pwr.averageW = pwr.averageW?.div(1000) }
                // Formula for kWh is (powerKw * deltaTimeH)
                // Time difference between the readings is already every hour
                // So kWh = powerKw, and we accumulate for every reading
                .fold(0.0) { acc, pwr -> acc + pwr.averageW!! }

            it.complete(TotalPower(totalKwh))
        }
    }
}