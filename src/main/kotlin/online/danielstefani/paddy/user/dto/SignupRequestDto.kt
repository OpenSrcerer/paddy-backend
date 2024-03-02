package online.danielstefani.paddy.user.dto

data class SignupRequestDto(
    val email: String,
    val username: String,
    val passwordHash: String, // Expected that this is a PBKDF2 of the password
)
