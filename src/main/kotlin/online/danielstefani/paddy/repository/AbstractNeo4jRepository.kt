package online.danielstefani.paddy.repository

import jakarta.inject.Inject
import org.neo4j.ogm.session.SessionFactory

/*
This file is just to create sugar syntax.
The repository implementations are under feature packages.
 */
abstract class AbstractNeo4jRepository {

    @Inject
    protected lateinit var neo4j: SessionFactory
}