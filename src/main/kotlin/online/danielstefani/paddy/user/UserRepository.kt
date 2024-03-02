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

    fun getForLogin(dto: LoginRequestDto): User? {
        with(neo4jSessionFactory.openSession()) {
            return this.queryForObject<User>(
                """
                    MATCH (node:User) 
                    WHERE (node.email = "${dto.emailOrUsername}" OR node.username = "${dto.emailOrUsername}")
                        AND node.passwordHash = "${dto.passwordHash}"
                    RETURN node
                """, emptyMap())
        }
    }

    fun create(
        email: String,
        username: String,
        passwordHash: String,
        passwordSalt: String
    ): User? {
        with(neo4jSessionFactory.openSession()) {
            val existingUser = this.queryForObject<User>(
                """
                    MATCH (node:User)
                    WHERE (node.email = "$email" OR node.username = "$username")
                    RETURN node
                """, emptyMap())
            if (existingUser != null)
                return null

            return User()
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