package online.danielstefani.paddy.repository

import io.quarkus.logging.Log
import jakarta.annotation.PreDestroy
import jakarta.enterprise.context.RequestScoped
import online.danielstefani.paddy.util.get
import org.neo4j.ogm.session.Session
import org.neo4j.ogm.session.queryForObject
import org.neo4j.ogm.transaction.Transaction

@RequestScoped
class RequestScopedNeo4jSession(
    private val sessionFactory: Neo4jSessionFactory
) {
    private var session: Session? = null

    inline fun <reified T : Any> queryWithTransaction(query: String): List<T> {
        return getSession().beginTransaction(Transaction.Type.READ_WRITE).use { tx ->
            try {
                val results = query<T>(query)
                tx.commit()
                results
            } catch (ex: Exception) {
                Log.error("[neo4j->session] Failed to execute transaction!", ex)
                tx.rollback()
                emptyList()
            } finally {
                tx.close()
            }
        }
    }

    inline fun <reified T : Any> queryForObject(query: String): T? {
        Log.info("[neo4j->session] <$query>")

        return getSession().queryForObject(query, mapOf())
    }

    inline fun <reified T : Any> query(query: String): List<T> {
        Log.info("[neo4j->session] <$query>")

        return getSession().query(query, mapOf<String, String>()).get()
    }

    operator fun invoke(): Session {
        return getSession()
    }

    fun getSession(): Session {
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