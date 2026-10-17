package ca.cairn.hms.application.dtos.queries

import java.util.UUID

data class SignUpForHikeQuery(
    val hikerId: UUID,
    val hikeId: UUID,
)
