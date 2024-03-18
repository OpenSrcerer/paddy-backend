package online.danielstefani.paddy.repository

import io.quarkus.logging.Log
import jakarta.enterprise.context.BeforeDestroyed
import jakarta.enterprise.context.Initialized
import jakarta.enterprise.context.RequestScoped
import jakarta.enterprise.event.Observes
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
            Log.info("[neo4j->session] Opened <$session>...")
            session = it
        }
    }

    internal fun onDestroy(@Observes @BeforeDestroyed(RequestScoped::class) destroy: Any) {
        Log.info("[neo4j->session] Destroying session <$session>...")
        session?.clear()
    }
}