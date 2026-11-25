package ca.cairn.hms.domain.hike

import ca.cairn.hms.domain.shared.Actor
import java.time.LocalDateTime

/** Objet-valeur : une étape du cycle de vie d'une randonnée et l'acteur qui l'a effectuée. */
data class StatusChange(
    val status: HikeStatus,
    val changedBy: Actor,
    val changedAt: LocalDateTime,
)
