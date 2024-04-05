package online.danielstefani.paddy.security

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
        if (identity == null)
            return Uni.createFrom().item(true)

        val path = event.pathParam("*")

        return identity.flatMap { id ->

//            // Only allow refresh tokens to access /refresh API to retrieve real token
//            if (id?.hasRole("refresh") == true) {
//                return@flatMap if (path.equals("refresh")) Uni.createFrom().item(true)
//                else Uni.createFrom().item(false)
//            }

            // This is to protect users from accessing other user's daemons
            return@flatMap if (path.startsWith("daemon"))
                authorizeDaemonRoute(path, id)
            else
                Uni.createFrom().item(true)

        }
    }

    @ActivateRequestContext
    fun authorizeDaemonRoute(
        path: String,
        identity: SecurityIdentity?
    ): Uni<Boolean> {
        val splitPath = path.split("/")
        if (splitPath.size <= 1) {
            return Uni.createFrom().item(true)
        }

        // splitPath[1] should be daemonId, try to parse as number
        try { splitPath[1].toLong() } catch (ex: NumberFormatException) {
            return Uni.createFrom().item(false)
        }

        return if (identity != null) securityService.hasAccessToDaemon(identity.username(), splitPath[1])
            else Uni.createFrom().item(false)
    }
}