package ca.cairn.hms.application.dtos.queries

import ca.cairn.hms.domain.hiker.SkillLevel
import java.time.LocalDate

data class RegisterHikerQuery(
    val invitationCode: String,
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val homeAddress: String,
    val dateOfBirth: LocalDate,
    val emergencyContact: EmergencyContactDto,
    val skillLevel: SkillLevel,
    val waiverAccepted: Boolean,
    /** Obligatoire si le randonneur a moins de 18 ans : nom du tuteur légal qui accepte la décharge. */
    val legalGuardianName: String? = null,
)
