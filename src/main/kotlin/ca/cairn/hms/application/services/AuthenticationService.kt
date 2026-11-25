package ca.cairn.hms.application.services

import java.util.UUID

/** Sous-domaine générique Identité : authentification et rôles, fournis par un service externe. */
interface AuthenticationService {
    fun isAuthenticated(userId: UUID): Boolean

    fun isAdministrator(userId: UUID): Boolean

    fun createCredentials(
        userId: UUID,
        email: String,
        password: String,
    )
}
