package online.danielstefani.paddy.stats.dto

import com.fasterxml.jackson.annotation.JsonFormat

class AveragePower {
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    var averageW: Double? = null

    var temporal: Long? = null
}