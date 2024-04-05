package online.danielstefani.paddy.security.dto

data class AuthenticationRequestDto(
    val jwt: String,                       // Expected to be a JWT
    val topic: String? = null,             // Topic that client wants to access
    val refreshTokenSerial: String? = null // Serial of the token (for refresh tokens only)
)
