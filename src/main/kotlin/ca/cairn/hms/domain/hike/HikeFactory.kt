package ca.cairn.hms.domain.hike

import ca.cairn.hms.domain.trail.TrailDifficulty
import java.time.LocalDateTime
import java.util.UUID

interface HikeFactory {
    /** Crée une randonnée ouverte avec un nouvel identifiant unique. */
    @Suppress("LongParameterList")
    fun createHike(
        organizerId: UUID,
        trailId: String,
        title: String,
        description: String,
        schedule: HikeSchedule,
        difficulty: TrailDifficulty,
        capacity: Int,
        createdAt: LocalDateTime,
    ): Hike
}
