package online.danielstefani.paddy.security

import io.quarkus.security.identity.SecurityIdentity
import io.quarkus.vertx.http.runtime.security.HttpSecurityPolicy
import io.quarkus.vertx.http.runtime.security.HttpSecurityPolicy.AuthorizationRequestContext
import io.smallrye.mutiny.Uni
import io.vertx.ext.web.RoutingContext
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaddyHttpAuthorizer : HttpSecurityPolicy {

    override fun name(): String {
        return "paddy-http-authorizer"
    }

    override fun checkPermission(
        event: RoutingContext,
        identity: Uni<SecurityIdentity?>?,
        requestContext: AuthorizationRequestContext?
    ): Uni<HttpSecurityPolicy.CheckResult> {
        if (authorize(event)) {
            return Uni.createFrom().item(HttpSecurityPolicy.CheckResult.PERMIT)
        }
        return Uni.createFrom().item(HttpSecurityPolicy.CheckResult.DENY)
    }

    // TODO: add authorization logic eventually
    private fun authorize(event: RoutingContext): Boolean {
        return true
    }
}