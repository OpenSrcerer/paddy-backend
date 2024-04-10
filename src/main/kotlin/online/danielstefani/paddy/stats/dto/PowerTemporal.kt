package online.danielstefani.paddy.stats.dto

enum class PowerTemporal(
    private val valueSeconds: Int
) {
    MINUTE(60),
    HOUR(MINUTE * 60),
    DAY(HOUR * 24),
    WEEK(DAY * 7),
    FOUR_WEEKS(WEEK * 4),
    QUARTER(FOUR_WEEKS * 3),
    HALF_YEAR(DAY * (365 / 2)),
    YEAR(DAY * 365);

    // Returns how many hours there are in any given unit
    fun toHours(temporal: PowerTemporal = this): Double {
        return when (temporal) {
            MINUTE      -> 1.0 / 60
            HOUR        -> toHours(MINUTE) * 60
            DAY         -> toHours(HOUR) * 24
            WEEK        -> toHours(DAY) * 7
            FOUR_WEEKS  -> toHours(WEEK) * 4
            QUARTER     -> toHours(FOUR_WEEKS) * 3
            HALF_YEAR   -> toHours(QUARTER) * 2
            YEAR        -> toHours(HALF_YEAR) * 2
        }
    }

    operator fun times(other: Int): Int = this.valueSeconds * other

    override fun toString(): String {
        return "$valueSeconds"
    }
}