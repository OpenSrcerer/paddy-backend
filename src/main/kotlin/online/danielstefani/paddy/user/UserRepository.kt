package online.danielstefani.paddy.user

import jakarta.enterprise.context.ApplicationScoped
import online.danielstefani.paddy.repository.AbstractNeo4jRepository
import java.util.*

@ApplicationScoped
class UserRepository : AbstractNeo4jRepository() {

    /*
    Email & Username takes the same value if one
    argument is provided because the given value
    could be email or username and that's logical.
     */
    fun get(email: String, username: String = email): User? {
        return session.queryForObject(
            """
                MATCH (node:User)
                WHERE (node.email = "$email" 
                    OR node.username = "$username")
                RETURN node
            """)
    }

    fun create(
        email: String,
        username: String,
        passwordHash: String,
        passwordSalt: String
    ): User? {
        return if (get(email, username) != null) null
        else User()
            .also {
                it.id = UUID.randomUUID().toString()
                it.email = email
                it.username = username
                it.passwordHash = passwordHash
                it.passwordSalt = passwordSalt

                session().save(it)
            }
    }
}