package ca.cairn.hms.application.dtos.queries

import java.util.UUID

data class CancelSignUpQuery(
    val hikerId: UUID,
    val signUpId: UUID,
)
