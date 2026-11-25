package ca.cairn.hms.domain.hike

import ca.cairn.hms.domain.shared.violation
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

/** Objet-valeur : date et heure de début, durée prévue et date limite d'inscription d'une randonnée. */
data class HikeSchedule(
    val startDateTime: LocalDateTime,
    val expectedDuration: Duration,
    val signUpDeadline: LocalDate,
) {
    init {
        val startDate = startDateTime.toLocalDate()
        if (signUpDeadline.isAfter(startDate)) violation("date limite après la date de la randonnée")
        if (signUpDeadline.isBefore(startDate.minusWeeks(MAX_DEADLINE_WEEKS_BEFORE))) {
            violation("date limite plus de 2 semaines avant la date")
        }
        if (expectedDuration.isNegative || expectedDuration.isZero) violation("durée invalide")
    }

    val startDate: LocalDate get() = startDateTime.toLocalDate()

    /** La date limite est atteinte le jour même de la date limite. */
    fun isDeadlineReached(today: LocalDate): Boolean = !today.isBefore(signUpDeadline)

    /** La date limite est passée le lendemain de la date limite. */
    fun isDeadlinePassed(today: LocalDate): Boolean = today.isAfter(signUpDeadline)

    fun hasStarted(now: LocalDateTime): Boolean = !now.isBefore(startDateTime)

    companion object {
        const val MAX_DEADLINE_WEEKS_BEFORE = 2L
    }
}
