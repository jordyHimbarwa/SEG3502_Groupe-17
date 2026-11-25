package ca.cairn.hms.application.usecases.implementation

import ca.cairn.hms.application.dtos.queries.RegisterHikerQuery
import ca.cairn.hms.application.services.AuthenticationService
import ca.cairn.hms.application.services.InvitationCodeService
import ca.cairn.hms.application.usecases.RegisterHiker
import ca.cairn.hms.domain.hiker.EmergencyContact
import ca.cairn.hms.domain.hiker.HikerFacade
import ca.cairn.hms.domain.hiker.PersonalInformation
import ca.cairn.hms.domain.shared.BusinessRuleViolationException
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.LocalDateTime
import java.util.UUID

@Service
class RegisterHikerImpl(
    private val invitationCodeService: InvitationCodeService,
    private val authenticationService: AuthenticationService,
    private val hikerFacade: HikerFacade,
    private val clock: Clock,
) : RegisterHiker {
    override fun registerHiker(query: RegisterHikerQuery): UUID {
        if (!invitationCodeService.isValid(query.invitationCode)) {
            throw BusinessRuleViolationException("code d'invitation invalide")
        }
        if (query.password.isBlank()) throw BusinessRuleViolationException("informations incomplètes")

        val hiker =
            hikerFacade.registerHiker(
                email = query.email,
                personalInformation =
                    PersonalInformation(query.firstName, query.lastName, query.phoneNumber, query.homeAddress),
                dateOfBirth = query.dateOfBirth,
                emergencyContact = EmergencyContact(query.emergencyContact.name, query.emergencyContact.phoneNumber),
                skillLevel = query.skillLevel,
                waiverAccepted = query.waiverAccepted,
                legalGuardianName = query.legalGuardianName,
                now = LocalDateTime.now(clock),
            )
        authenticationService.createCredentials(hiker.id, hiker.email, query.password)
        invitationCodeService.markUsed(query.invitationCode)
        return hiker.id
    }
}
