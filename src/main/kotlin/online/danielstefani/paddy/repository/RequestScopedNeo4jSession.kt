package online.danielstefani.paddy.repository

import io.quarkus.logging.Log
import jakarta.annotation.PreDestroy
import jakarta.enterprise.context.RequestScoped
import org.neo4j.ogm.session.Session

@RequestScoped
class RequestScopedNeo4jSession(
    private val sessionFactory: Neo4jSessionFactory
) {
    private var session: Session? = null

    operator fun invoke(): Session {
        return get()
    }

    private fun get(): Session {
        return session ?: sessionFactory.get().also {
            session = it
            Log.debug("[neo4j->session] Opened <$session>...")
        }
    }

    @PreDestroy
    internal fun onDestroy() {
        Log.debug("[neo4j->session] Destroying session <$session>...")
        session?.clear()
    }
}