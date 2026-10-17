package ca.cairn.hms.application.dtos.queries

import ca.cairn.hms.domain.trail.TrailDifficulty
import java.time.LocalDate
import java.util.UUID

/** Enregistre une certification de chef de randonnée, ce qui fait du randonneur un organisateur. */
data class RecordLeaderCertificationQuery(
    val administratorId: UUID,
    val hikerId: UUID,
    val certificateNumber: String,
    val issueDate: LocalDate,
    val expiryDate: LocalDate,
    /** Difficulté la plus élevée que l'organisateur peut diriger. */
    val organizerLevel: TrailDifficulty,
)
