package online.danielstefani.paddy.pad

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.neo4j.ogm.session.SessionFactory
import org.neo4j.ogm.session.queryForObject

@ApplicationScoped
class PadRepository {

    @Inject
    private lateinit var neo4jSessionFactory: SessionFactory

//    fun upsert(serial: String, jwt: String): Pad {
//        with(neo4jSessionFactory.openSession()) {
//
//        }
//    }

}