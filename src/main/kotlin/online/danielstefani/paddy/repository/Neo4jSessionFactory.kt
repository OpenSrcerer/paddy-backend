package online.danielstefani.paddy.repository

import jakarta.enterprise.context.ApplicationScoped
import org.neo4j.ogm.session.Session
import org.neo4j.ogm.session.SessionFactory

@ApplicationScoped
class Neo4jSessionFactory(
    private val factory: SessionFactory
) {
    fun get(): Session {
        return factory.openSession()
    }
}