package ca.cairn.hms.application.dtos.results

import ca.cairn.hms.domain.hike.HikeStatus
import ca.cairn.hms.domain.trail.TrailDifficulty
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

/** Randonnée telle qu'affichée par Parcourir les randonnées. */
data class HikeSummaryDto(
    val hikeId: UUID,
    val title: String,
    val description: String,
    val trailName: String,
    val difficulty: TrailDifficulty,
    val startDateTime: LocalDateTime,
    val expectedDuration: Duration,
    val signUpDeadline: LocalDate,
    val organizerName: String,
    val remainingSpots: Int,
    val status: HikeStatus,
)
