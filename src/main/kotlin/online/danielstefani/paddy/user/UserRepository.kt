package online.danielstefani.paddy.user

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import online.danielstefani.paddy.session.dto.LoginRequestDto
import org.neo4j.ogm.session.SessionFactory
import org.neo4j.ogm.session.queryForObject
import java.util.*

@ApplicationScoped
class UserRepository {

    @Inject
    private lateinit var neo4jSessionFactory: SessionFactory

    fun get(dto: LoginRequestDto): User? {
        with(neo4jSessionFactory.openSession()) {
            return this.queryForObject<User>(
                """
                    MATCH (node:User) 
                    WHERE (node.email = ${dto.emailOrUsername} OR node.username = ${dto.emailOrUsername})
                        AND node.password = ${dto.passwordHash}
                    RETURN node
                """, emptyMap())
        }
    }

    fun create(
        email: String,
        username: String,
        passwordHash: String
    ): User? {
        with(neo4jSessionFactory.openSession()) {
            val existingUser = this.queryForObject<User>(
                """
                    MATCH (node:User)
                    WHERE (node.email = $email OR node.username = $username)
                    RETURN node
                """, emptyMap())
            if (existingUser != null)
                return null

            return User(UUID.randomUUID(), email, username, passwordHash, emptySet())
                .also { this.save(it) }
        }
    }
}