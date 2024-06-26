package online.danielstefani.paddy.jwt.dto

data class JwtRequestDto(
    val subject: String,
    val jwtType: JwtType,
    val refreshTokenSerial: String? = null
)
