package ca.cairn.hms.application.dtos.queries

import ca.cairn.hms.domain.trail.TrailDifficulty
import java.time.LocalDate

/** Critères de recherche des randonnées ouvertes. Tous les critères sont facultatifs et se combinent (ET). */
data class SearchOpenHikesQuery(
    val trailName: String? = null,
    val date: LocalDate? = null,
    val difficulty: TrailDifficulty? = null,
)
