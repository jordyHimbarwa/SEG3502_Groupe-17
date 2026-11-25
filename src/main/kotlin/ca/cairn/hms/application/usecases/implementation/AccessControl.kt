package ca.cairn.hms.application.usecases.implementation

import ca.cairn.hms.application.services.AuthenticationService
import ca.cairn.hms.domain.shared.UnauthenticatedException
import ca.cairn.hms.domain.shared.UnauthorizedException
import java.util.UUID

internal fun AuthenticationService.requireAuthenticated(userId: UUID) {
    if (!isAuthenticated(userId)) throw UnauthenticatedException()
}

internal fun AuthenticationService.requireAdministrator(userId: UUID) {
    requireAuthenticated(userId)
    if (!isAdministrator(userId)) throw UnauthorizedException()
}
