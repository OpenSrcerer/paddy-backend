package online.danielstefani.paddy.pad

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.neo4j.ogm.session.SessionFactory
import org.neo4j.ogm.session.queryForObject

@ApplicationScoped
class PadRepository {

    @Inject
    private lateinit var neo4jSessionFactory: SessionFactory

    fun upsert(serial: String, jwt: String): Pad {
        with(neo4jSessionFactory.openSession()) {
            val existingPad = this.queryForObject<Pad>(
                "MATCH (deviceNode:Device {serial: $serial}) RETURN deviceNode",
                emptyMap()
            )
            val newPad = Pad(serial, jwt)

            if (existingPad != null)
                this.save(existingPad.also { it.jwt = jwt })
            else
                this.save(newPad)

            return existingPad ?: newPad
        }
    }

}