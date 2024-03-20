package online.danielstefani.paddy.security

import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import online.danielstefani.paddy.daemon.Daemon
import online.danielstefani.paddy.repository.RequestScopedNeo4jSession
import org.neo4j.ogm.session.queryForObject

@ApplicationScoped
class HttpSecurityRepository(
    private val session: RequestScopedNeo4jSession
) {
    fun getUserDaemon(username: String, daemonId: String): Uni<Daemon?> {
        val query = """
                        MATCH (ux:User { username: "$username" }) 
                            -[:OWNS]-> (dx:Daemon)
                        WHERE dx.id = "$daemonId"
                        RETURN dx
                    """

        return Uni.createFrom().emitter {
            it.complete(session().queryForObject<Daemon>(query, emptyMap()))
        }
    }
}