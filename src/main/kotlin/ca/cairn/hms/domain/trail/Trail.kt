package ca.cairn.hms.domain.trail

import java.time.LocalDate

/**
 * Entité racine de l'agrégat Sentier. Les données proviennent de la CCN ;
 * le système en conserve une copie locale.
 */
class Trail(
    val id: String,
    val name: String,
    val difficulty: TrailDifficulty,
    val openingSeason: OpeningSeason,
    val hikingAllowed: Boolean,
    val startPoint: String,
) {
    fun isOpenOn(date: LocalDate): Boolean = openingSeason.contains(date)
}
