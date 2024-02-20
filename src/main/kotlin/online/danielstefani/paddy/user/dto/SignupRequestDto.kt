package online.danielstefani.paddy.user.dto

data class SignupRequestDto(
    val email: String,
    val username: String,
    val passwordHash: String // Expected that this is a SHA-256 of the password
)
