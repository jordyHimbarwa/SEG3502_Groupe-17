package ca.cairn.hms.domain.hiker

import ca.cairn.hms.domain.shared.violation
import ca.cairn.hms.domain.trail.TrailDifficulty
import java.time.LocalDate

data class PersonalInformation(
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val homeAddress: String,
) {
    fun isComplete(): Boolean = listOf(firstName, lastName, phoneNumber, homeAddress).none { it.isBlank() }
}

data class EmergencyContact(
    val name: String,
    val phoneNumber: String,
) {
    fun isComplete(): Boolean = name.isNotBlank() && phoneNumber.isNotBlank()
}

/** Décharge de responsabilité ; acceptée par un tuteur légal lorsque le randonneur est mineur. */
data class Waiver(
    val acceptedOn: LocalDate,
    val legalGuardianName: String? = null,
)

/** Certification de chef de randonnée. Sa présence fait du randonneur un organisateur. */
data class LeaderCertification(
    val certificateNumber: String,
    val issueDate: LocalDate,
    val expiryDate: LocalDate,
    /** Difficulté la plus élevée que l'organisateur peut diriger. */
    val organizerLevel: TrailDifficulty,
) {
    init {
        if (certificateNumber.isBlank()) violation("informations incomplètes")
        if (!expiryDate.isAfter(issueDate)) violation("date d'expiration antérieure à la date de délivrance")
    }

    fun isValidOn(date: LocalDate): Boolean = !date.isBefore(issueDate) && !date.isAfter(expiryDate)

    fun allowsLeading(difficulty: TrailDifficulty): Boolean = difficulty <= organizerLevel
}
