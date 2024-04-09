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

    operator fun times(other: Int): Int = this.valueSeconds * other

    override fun toString(): String {
        return "$valueSeconds"
    }
}