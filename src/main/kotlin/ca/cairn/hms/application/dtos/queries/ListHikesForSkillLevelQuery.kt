package ca.cairn.hms.application.dtos.queries

import java.util.UUID

/** Randonnées ouvertes correspondant au niveau d'habileté du randonneur connecté. */
data class ListHikesForSkillLevelQuery(
    val hikerId: UUID,
)
