package online.danielstefani.paddy.security

import io.quarkus.logging.Log
import io.quarkus.security.identity.IdentityProviderManager
import io.quarkus.security.identity.SecurityIdentity
import io.quarkus.security.identity.request.AuthenticationRequest
import io.quarkus.security.identity.request.TokenAuthenticationRequest
import io.quarkus.security.runtime.QuarkusSecurityIdentity
import io.quarkus.vertx.http.runtime.security.ChallengeData
import io.quarkus.vertx.http.runtime.security.HttpAuthenticationMechanism
import io.smallrye.mutiny.Uni
import io.vertx.ext.web.RoutingContext
import jakarta.annotation.Priority
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.context.control.ActivateRequestContext
import jakarta.enterprise.inject.Alternative
import online.danielstefani.paddy.jwt.JwtAuthClient

import online.danielstefani.paddy.security.dto.AuthenticationRequestDto
import online.danielstefani.paddy.security.dto.AuthenticationResultDto
import online.danielstefani.paddy.security.dto.AuthenticationResultDto.AuthenticationResult
import org.eclipse.microprofile.rest.client.inject.RestClient


@Alternative
@Priority(1)
@ApplicationScoped
class HttpAuthenticator(
    private val repository: HttpSecurityRepository,
    @RestClient private val paddyAuth: JwtAuthClient
) : HttpAuthenticationMechanism {

    /*
    Ask the authentication service whether the token is valid.
    If the token is valid, set the principal and role to the "sub"
    claim of the JWT (the server should return it on the
    "resource" field of the DTO)
    */
    override fun authenticate(
        context: RoutingContext?,
        identityProviderManager: IdentityProviderManager?
    ): Uni<SecurityIdentity> {
        val jwt = context!!.request().getHeader("Authorization")?.replace("Bearer ", "")

        return checkJwt(jwt)
            .onItem().invoke { res -> Log.info("Principal: ${res.resource} // Access Result: ${res.result}") }
            .onItem().transform {
                with(QuarkusSecurityIdentity.builder()) {
                    setPrincipal { it.resource!! }

                    when (it.result) {
                        AuthenticationResult.REFRESH -> addRole("refresh")
                        AuthenticationResult.ALLOW -> addRole(it.resource!!)
                        else -> setAnonymous(true)
                    }

                    build()
                }
            }
    }

    @ActivateRequestContext
    protected fun checkJwt(jwt: String?): Uni<AuthenticationResultDto> {
        // Don't contact auth server is bearer is missing
        if (jwt == null) {
            return Uni.createFrom().item(
                AuthenticationResultDto(AuthenticationResult.DENY, "<missing jwt>"))
        }

        return paddyAuth.validateJwt(AuthenticationRequestDto(jwt))
            .flatMap { authRes ->
                // Regular case: Non-refresh token
                if (authRes.result != AuthenticationResult.REFRESH)
                    return@flatMap Uni.createFrom().item(authRes)

                // For refresh tokens we have to check with the auth server twice.
                // This is because first we 1) check the validity of the token (signature).
                // Then, 2) we extract the "sub" from it, get the user's Refresh Token Serial,
                // and validate the token again.
                return@flatMap repository.getUserRts(authRes.resource!!)
                    .flatMap { paddyAuth.validateJwt(AuthenticationRequestDto(jwt, refreshTokenSerial = it)) }
            }
    }

    /*
    Implementation for generating challenge data in case of authentication failure
    */
    override fun getChallenge(context: RoutingContext?): Uni<ChallengeData> {
        return Uni.createFrom()
            .item(ChallengeData(401, "WWW-Authenticate", "Bearer"))
    }

    /*
    Matches with PaddyHttpIdentityProvider. If that class doesn't exist, this will not work.
    To experiment further regarding why this is.
     */
    override fun getCredentialTypes(): Set<Class<out AuthenticationRequest>> {
        return setOf(TokenAuthenticationRequest::class.java)
    }
}