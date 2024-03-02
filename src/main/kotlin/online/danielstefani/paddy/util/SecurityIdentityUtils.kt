package online.danielstefani.paddy.util

import io.quarkus.security.identity.SecurityIdentity
import java.security.Security

fun SecurityIdentity.username(): String {
    return this.principal.name
}