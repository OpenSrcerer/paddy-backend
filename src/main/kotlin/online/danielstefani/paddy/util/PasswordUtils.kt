package online.danielstefani.paddy.util

import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

private val PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA512"
private val PBKDF2_ITERATIONS = 210000 // From OWASP
private val PBKDF2_KEY_LENGTH = 256 // Bits
private val PBKDF2_SALT_LENGTH = 128 // Bits

@OptIn(ExperimentalEncodingApi::class)
fun generatePBKHash(passwordHash: String, salt: String? = null): Pair<ByteArray, ByteArray> {
    val computedSalt =
        if (salt != null) Base64.decode(salt.toString())
        else with(SecureRandom.getInstanceStrong()) {
            ByteArray(PBKDF2_SALT_LENGTH / 8).also { this.nextBytes(it) }
        }

    val hashedx2Password = with(SecretKeyFactory.getInstance(PBKDF2_ALGORITHM)) {
        val spec = PBEKeySpec(passwordHash.toCharArray(), computedSalt,
            PBKDF2_ITERATIONS, PBKDF2_KEY_LENGTH)

        this.generateSecret(spec).encoded
    }

    return Pair(hashedx2Password, computedSalt)
}

/*
Returns a Pair<Hashed2Password, Salt> in Base64
 */
@OptIn(ExperimentalEncodingApi::class)
fun generatePBKHashBase64(passwordHash: String): Pair<String, String> {
    val (hx2Password, salt) = generatePBKHash(passwordHash)

    return Pair(
        Base64.encode(hx2Password),
        Base64.encode(salt))
}

/*
 Both given parameters are Base64-encoded
 */
@OptIn(ExperimentalEncodingApi::class)
fun isPasswordHashMatch(
    givenPasswordHash: String,
    actualPasswordHash: String,
    salt: String
): Boolean {
    val (computedGivenHash, _) = generatePBKHash(givenPasswordHash, salt)
    return computedGivenHash.contentEquals(Base64.decode(actualPasswordHash))
}