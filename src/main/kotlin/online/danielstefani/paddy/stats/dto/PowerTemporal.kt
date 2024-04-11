package online.danielstefani.paddy.stats.dto

enum class PowerTemporal(
    private val valueSeconds: Int
) {
    MINUTE(60),
    HOUR(MINUTE * 60),
    DAY(HOUR * 24),
    WEEK(DAY * 7),
    MONTH(2_628_000),
    QUARTER(MONTH * 3),
    HALF_YEAR(QUARTER * 2),
    YEAR(HALF_YEAR * 2);

    operator fun times(other: Int): Int = this.valueSeconds * other

    override fun toString(): String {
        return "$valueSeconds"
    }
}