package online.danielstefani.paddy.repository

import jakarta.inject.Inject

/*
This file is just to create sugar syntax.
The repository implementations are under feature packages.
 */
abstract class AbstractNeo4jRepository {

    @Inject
    protected lateinit var session: RequestScopedNeo4jSession
}