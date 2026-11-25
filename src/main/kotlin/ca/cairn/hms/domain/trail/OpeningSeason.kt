package ca.cairn.hms.domain.trail

import java.time.LocalDate
import java.time.MonthDay

/** Objet-valeur : période de l'année pendant laquelle un sentier est ouvert (bornes incluses). */
data class OpeningSeason(val start: MonthDay, val end: MonthDay) {
    fun contains(date: LocalDate): Boolean {
        val day = MonthDay.from(date)
        return if (!start.isAfter(end)) {
            !day.isBefore(start) && !day.isAfter(end)
        } else {
            // Saison qui chevauche le 1er janvier (p. ex. l'hiver).
            !day.isBefore(start) || !day.isAfter(end)
        }
    }

    companion object {
        val ALL_YEAR = OpeningSeason(MonthDay.of(1, 1), MonthDay.of(12, 31))
        val SPRING = OpeningSeason(MonthDay.of(3, 1), MonthDay.of(5, 31))
        val SUMMER = OpeningSeason(MonthDay.of(6, 1), MonthDay.of(9, 30))
        val FALL = OpeningSeason(MonthDay.of(9, 1), MonthDay.of(11, 30))
        val WINTER = OpeningSeason(MonthDay.of(12, 1), MonthDay.of(3, 31))
    }
}
