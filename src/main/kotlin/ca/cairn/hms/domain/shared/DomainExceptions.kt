package ca.cairn.hms.domain.shared

open class DomainException(message: String) : RuntimeException(message)

/** Une règle métier n'est pas respectée ; [reason] est le motif présenté à l'utilisateur. */
class BusinessRuleViolationException(val reason: String) : DomainException(reason)

/** L'acteur est authentifié mais n'a pas le droit d'effectuer l'action. */
class UnauthorizedException(message: String = "défaut d'autorisation") : DomainException(message)

/** L'action exige un utilisateur authentifié. */
class UnauthenticatedException(message: String = "défaut d'authentification") : DomainException(message)

class EntityNotFoundException(message: String) : DomainException(message)

internal fun violation(reason: String): Nothing = throw BusinessRuleViolationException(reason)
