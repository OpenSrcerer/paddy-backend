package online.danielstefani.paddy.util

import io.quarkus.security.identity.SecurityIdentity

fun SecurityIdentity.username(): String {
    return this.principal.name
}