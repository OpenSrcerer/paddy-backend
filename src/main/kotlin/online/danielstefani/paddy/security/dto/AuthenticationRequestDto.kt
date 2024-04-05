package online.danielstefani.paddy.security.dto

data class AuthenticationRequestDto(
    val jwt: String,              // Expected to be a JWT
    val topic: String? = null,    // Topic that client wants to access
    val refresh: Boolean = false  // Does this payload represent a refresh token request
)
