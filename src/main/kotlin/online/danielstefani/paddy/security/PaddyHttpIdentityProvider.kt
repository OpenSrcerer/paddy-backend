package online.danielstefani.paddy.security

import io.quarkus.security.identity.AuthenticationRequestContext
import io.quarkus.security.identity.IdentityProvider
import io.quarkus.security.identity.SecurityIdentity
import io.quarkus.security.identity.request.TokenAuthenticationRequest
import io.smallrye.mutiny.Uni
import jakarta.annotation.Priority
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Alternative

@Alternative
@Priority(1)
@ApplicationScoped
class PaddyHttpIdentityProvider : IdentityProvider<TokenAuthenticationRequest> {

    override fun getRequestType(): Class<TokenAuthenticationRequest> {
        return TokenAuthenticationRequest::class.java
    }

    override fun authenticate(
        tar: TokenAuthenticationRequest?,
        arc: AuthenticationRequestContext?
    ): Uni<SecurityIdentity> {
        throw UnsupportedOperationException()
    }
}