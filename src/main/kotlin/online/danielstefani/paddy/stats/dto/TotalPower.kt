package online.danielstefani.paddy.stats.dto

import com.fasterxml.jackson.annotation.JsonFormat

data class TotalPower(
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    val kWh: Double
)