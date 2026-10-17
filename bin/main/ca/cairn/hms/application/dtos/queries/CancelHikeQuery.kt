package ca.cairn.hms.application.dtos.queries

import java.util.UUID

/** Annulation par l'organisateur de la randonnée ou par un administrateur. */
data class CancelHikeQuery(
    val requesterId: UUID,
    val hikeId: UUID,
    val reason: String,
)
