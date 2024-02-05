package online.danielstefani.paddy.security

import jakarta.enterprise.context.ApplicationScoped
import online.danielstefani.paddy.client.MqttConfiguration
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.Signature
import java.security.interfaces.RSAPrivateCrtKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.RSAPublicKeySpec
import java.time.Instant
import java.util.Base64;

@ApplicationScoped
class JwtService(
    private val mqttConfiguration: MqttConfiguration
) {
    fun makeJwks(): String {
        val privateKeyContent =
            mqttConfiguration.authenticationKey()
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("\n", "")
                .replace(" ", "")

        // ******************
        // Create private key
        // ******************
        val keyFactory = KeyFactory.getInstance("RSA")
        val privateKeySpec = PKCS8EncodedKeySpec(
            Base64.getDecoder().decode(privateKeyContent)
        )
        val privateKey = keyFactory.generatePrivate(privateKeySpec)


        // *****************
        // Create public key
        // *****************
        val publicKeySpec = RSAPublicKeySpec(
            (privateKey as RSAPrivateCrtKey).modulus,
            privateKey.publicExponent
        )
        val publicKey = keyFactory.generatePublic(publicKeySpec)


        // *********************
        // Prepare JWKS response
        // *********************
        val rsa = publicKey as RSAPublicKey

        val jwksResponse = java.lang.String.format(
            """
            {
                "keys": [{
                    "kty": "%s",
                    "kid": "1",
                    "n": "%s",
                    "e": "%s",
                    "alg": "RS256",
                    "use": "sig"
                }]
            }
            """.trimIndent()
                .replace("\n", "")
                .replace(" ", ""),
            rsa.algorithm,
            Base64.getUrlEncoder().encodeToString(rsa.modulus.toByteArray()),
            Base64.getUrlEncoder().encodeToString(rsa.publicExponent.toByteArray())
        )

        return jwksResponse
    }

    fun makeJwt(): String {
        val privateKeyContent =
            mqttConfiguration.authenticationKey()
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("\n", "")
                .replace(" ", "")

        // ******************
        // Create private key
        // ******************
        val keyFactory = KeyFactory.getInstance("RSA")
        val privateKeySpec = PKCS8EncodedKeySpec(
            Base64.getDecoder().decode(privateKeyContent)
        )
        val privateKey = keyFactory.generatePrivate(privateKeySpec)

        val jwtHeader: String = Base64.getUrlEncoder().withoutPadding().encodeToString(
            """
            { "alg": "RS256", "typ": "JWT", "kid": "1" }
            
            """.trimIndent().replace(" ", "").replace("\n", "")
                .toByteArray(StandardCharsets.UTF_8)
        )

        val jwtPayloadTemplate = """
            {
                "sub": "daniel.stefani",
                "iss": "https://danielstefani.online",
                "iat": %s,
                "exp": %s,
                "aud": "MQTT Clients"
            }
            
            """.trimIndent().replace(" ", "").replace("\n", "")

        val jwtPayload: String = Base64.getUrlEncoder().withoutPadding().encodeToString(
            String.format(
                jwtPayloadTemplate,
                Instant.now().epochSecond,
                Instant.now().plusSeconds(300).epochSecond
            ).replace(" ", "").replace("\n", "")
                .toByteArray(StandardCharsets.UTF_8)
        )

        val jwtContent = "$jwtHeader.$jwtPayload"
        val signature = Signature.getInstance("SHA256withRSA")
        signature.initSign(privateKey)


        val jwtSignature: String
        signature.update(jwtContent.toByteArray())
        jwtSignature = Base64.getUrlEncoder().withoutPadding().encodeToString(
            signature.sign()
        )

        return "$jwtContent.$jwtSignature"
    }
}