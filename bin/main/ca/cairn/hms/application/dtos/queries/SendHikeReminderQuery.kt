package ca.cairn.hms.application.dtos.queries

import java.util.UUID

/** Déclenchée par le système la veille de la randonnée. */
data class SendHikeReminderQuery(
    val hikeId: UUID,
)
