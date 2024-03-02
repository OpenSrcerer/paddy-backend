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

    /*
    Email & Username takes the same value if one
    argument is provided because the given value
    could be email or username and that's logical.
     */
    fun get(email: String, username: String = email): User? {
        with(neo4jSessionFactory.openSession()) {
            val existingUser = this.queryForObject<User>(
                """
                    MATCH (node:User)
                    WHERE (node.email = "$email" 
                        OR node.username = "$username")
                    RETURN node
                """, emptyMap()
            )

            return if (existingUser != null) existingUser else null
        }
    }

    fun create(
        email: String,
        username: String,
        passwordHash: String,
        passwordSalt: String
    ): User? {
        with(neo4jSessionFactory.openSession()) {
            return if (get(email, username) != null) null
            else User()
                .also {
                    it.id = UUID.randomUUID().toString()
                    it.email = email
                    it.username = username
                    it.passwordHash = passwordHash
                    it.passwordSalt = passwordSalt

                    this.save(it)
                }
        }
    }
}