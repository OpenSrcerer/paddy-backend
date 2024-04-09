package online.danielstefani.paddy.stats

import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import online.danielstefani.paddy.power.PowerRepository
import online.danielstefani.paddy.stats.dto.PowerTemporal
import online.danielstefani.paddy.stats.dto.TotalPower

@ApplicationScoped
class StatsService(
    private val powerRepository: PowerRepository,
    private val statsRepository: StatsRepository
) {
    fun getTotalPower(daemonId: String): Uni<TotalPower> {
        return Uni.createFrom().emitter {
            val totalKwh = statsRepository.getAveragePowerEveryTemporal(daemonId, PowerTemporal.HOUR, null)
                // Turn all measurements to kW
                .onEach { pwr -> pwr.averageW = pwr.averageW?.div(1000) }
                // Formula for kWh is (powerKw * deltaTimeH)
                // Time difference between the readings is already every hour
                // So kWh = powerKw, and we accumulate for every reading
                .fold(0F) { acc, pwr -> acc + pwr.averageW!! }

            it.complete(TotalPower(totalKwh))
        }
    }
}