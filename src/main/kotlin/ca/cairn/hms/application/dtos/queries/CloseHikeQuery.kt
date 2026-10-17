package ca.cairn.hms.application.dtos.queries

import java.util.UUID

/** Déclenchée par le système lorsque la date limite d'inscription est atteinte. */
data class CloseHikeQuery(
    val hikeId: UUID,
)
