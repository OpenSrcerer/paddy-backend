package online.danielstefani.paddy.session.dto

data class LoginRequestDto(
    val emailOrUsername: String,
    val passwordHash: String
)