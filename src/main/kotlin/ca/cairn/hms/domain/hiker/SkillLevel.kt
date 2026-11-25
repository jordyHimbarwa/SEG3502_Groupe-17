package ca.cairn.hms.domain.hiker

import ca.cairn.hms.domain.trail.TrailDifficulty

/** Niveau d'habileté d'un randonneur, aligné un pour un sur [TrailDifficulty]. */
enum class SkillLevel {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED,
    EXPERT,
    ;

    /** Un randonneur peut s'inscrire à une randonnée plus facile, de son niveau ou d'au plus un niveau au-dessus. */
    fun canJoin(difficulty: TrailDifficulty): Boolean = difficulty.ordinal <= ordinal + 1
}
