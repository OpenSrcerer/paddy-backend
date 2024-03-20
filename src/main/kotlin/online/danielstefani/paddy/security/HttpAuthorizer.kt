package online.danielstefani.paddy.security

import io.quarkus.logging.Log
import io.quarkus.security.identity.SecurityIdentity
import io.quarkus.vertx.http.runtime.security.HttpSecurityPolicy
import io.quarkus.vertx.http.runtime.security.HttpSecurityPolicy.AuthorizationRequestContext
import io.smallrye.mutiny.Uni
import io.vertx.ext.web.RoutingContext
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.context.control.ActivateRequestContext
import online.danielstefani.paddy.util.username

@ApplicationScoped
class HttpAuthorizer(
    private val securityService: HttpSecurityService
) : HttpSecurityPolicy {

    override fun name(): String {
        return "paddy-http-authorizer"
    }

    override fun checkPermission(
        event: RoutingContext,
        identity: Uni<SecurityIdentity?>?,
        requestContext: AuthorizationRequestContext?
    ): Uni<HttpSecurityPolicy.CheckResult> {
        return authorize(event, identity)
            .map {
                if (it == true) HttpSecurityPolicy.CheckResult.PERMIT
                else HttpSecurityPolicy.CheckResult.DENY
            }
    }

    private fun authorize(
        event: RoutingContext,
        identity: Uni<SecurityIdentity?>?
    ): Uni<Boolean> {
        if (identity == null) {
            return Uni.createFrom().item(true)
        }

        val path = event.pathParam("*")
        if (path.startsWith("daemon")) {
            return authorizeDaemonRoute(path, identity)
        }

        return Uni.createFrom().item(true)
    }

    @ActivateRequestContext
    fun authorizeDaemonRoute(
        path: String,
        identity: Uni<SecurityIdentity?>
    ): Uni<Boolean> {
        val splitPath = path.split("/")
        if (splitPath.size <= 1) {
            return Uni.createFrom().item(true)
        }

        return identity
            .flatMap {
                if (it != null) securityService.hasAccessToDaemon(it.username(), splitPath[1])
                else Uni.createFrom().item(false)
            }
    }
}