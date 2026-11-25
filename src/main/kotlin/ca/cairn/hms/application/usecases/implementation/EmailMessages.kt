package ca.cairn.hms.application.usecases.implementation

import ca.cairn.hms.application.services.EmailMessage
import ca.cairn.hms.domain.hike.Hike
import ca.cairn.hms.domain.hiker.Hiker
import ca.cairn.hms.domain.trail.Trail
import java.time.format.DateTimeFormatter

/** Contenu des courriels envoyés aux randonneurs. */
object EmailMessages {
    const val SIGN_UP_CONFIRMED_SUBJECT = "Confirmation de votre inscription"
    const val WAITLISTED_SUBJECT = "Inscription en liste d'attente"
    const val PROMOTED_SUBJECT = "Votre inscription est maintenant confirmée"
    const val HIKE_CANCELLED_SUBJECT = "Randonnée annulée"
    const val REMINDER_SUBJECT = "Rappel : votre randonnée a lieu demain"
    const val WEATHER_WARNING = "Avertissement météorologique : de la pluie ou de la neige est prévue."

    private val DATE = DateTimeFormatter.ISO_LOCAL_DATE
    private val TIME = DateTimeFormatter.ofPattern("HH:mm")

    fun signUpConfirmed(
        hiker: Hiker,
        hike: Hike,
    ) = EmailMessage(
        hiker.email,
        SIGN_UP_CONFIRMED_SUBJECT,
        "Bonjour ${hiker.fullName}, votre inscription à « ${hike.title} » du ${hike.schedule.startDate.format(DATE)} est confirmée.",
    )

    fun waitlisted(
        hiker: Hiker,
        hike: Hike,
        position: Int,
    ) = EmailMessage(
        hiker.email,
        WAITLISTED_SUBJECT,
        "Bonjour ${hiker.fullName}, la randonnée « ${hike.title} » est complète. " +
            "Vous êtes en position $position dans la liste d'attente.",
    )

    fun promoted(
        hiker: Hiker,
        hike: Hike,
    ) = EmailMessage(
        hiker.email,
        PROMOTED_SUBJECT,
        "Bonjour ${hiker.fullName}, une place s'est libérée : votre inscription à « ${hike.title} » est maintenant confirmée.",
    )

    fun hikeCancelled(
        hiker: Hiker,
        hike: Hike,
        reason: String,
    ) = EmailMessage(
        hiker.email,
        HIKE_CANCELLED_SUBJECT,
        "Bonjour ${hiker.fullName}, la randonnée « ${hike.title} » du ${hike.schedule.startDate.format(DATE)} est annulée. " +
            "Motif : $reason",
    )

    fun reminder(
        hiker: Hiker,
        hike: Hike,
        trail: Trail?,
        weatherWarning: Boolean,
    ): EmailMessage {
        val body =
            buildString {
                append("Bonjour ${hiker.fullName}, rappel de la randonnée « ${hike.title} ».\n")
                append("Date : ${hike.schedule.startDate.format(DATE)}\n")
                append("Heure de début : ${hike.schedule.startDateTime.format(TIME)}\n")
                append("Sentier : ${trail?.name ?: hike.trailId}\n")
                append("Point de départ : ${trail?.startPoint ?: "à confirmer"}\n")
                if (weatherWarning) append(WEATHER_WARNING)
            }
        return EmailMessage(hiker.email, REMINDER_SUBJECT, body)
    }
}
