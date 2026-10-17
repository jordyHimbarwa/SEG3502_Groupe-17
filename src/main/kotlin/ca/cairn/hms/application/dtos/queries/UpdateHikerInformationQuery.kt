package ca.cairn.hms.application.dtos.queries

import ca.cairn.hms.domain.hiker.SkillLevel
import java.util.UUID

/** Mise à jour de ses propres informations par un randonneur. Le courriel (identifiant unique) n'est pas modifiable. */
data class UpdateHikerInformationQuery(
    val hikerId: UUID,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val homeAddress: String,
    val emergencyContact: EmergencyContactDto,
    val skillLevel: SkillLevel,
)
