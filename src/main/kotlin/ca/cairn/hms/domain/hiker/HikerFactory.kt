package ca.cairn.hms.domain.hiker

import java.time.LocalDate

interface HikerFactory {
    /** Crée un randonneur avec une adhésion active et un nouvel identifiant. */
    fun createHiker(
        email: String,
        personalInformation: PersonalInformation,
        dateOfBirth: LocalDate,
        emergencyContact: EmergencyContact,
        skillLevel: SkillLevel,
        waiver: Waiver,
    ): Hiker
}
