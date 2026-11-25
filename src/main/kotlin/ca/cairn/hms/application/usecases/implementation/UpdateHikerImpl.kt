package ca.cairn.hms.application.usecases.implementation

import ca.cairn.hms.application.dtos.queries.RecordLeaderCertificationQuery
import ca.cairn.hms.application.dtos.queries.UpdateHikerInformationQuery
import ca.cairn.hms.application.dtos.queries.UpdateMembershipStatusQuery
import ca.cairn.hms.application.services.AuthenticationService
import ca.cairn.hms.application.usecases.UpdateHiker
import ca.cairn.hms.domain.hiker.EmergencyContact
import ca.cairn.hms.domain.hiker.HikerFacade
import ca.cairn.hms.domain.hiker.PersonalInformation
import ca.cairn.hms.domain.shared.UnauthorizedException
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.LocalDateTime

@Service
class UpdateHikerImpl(
    private val authenticationService: AuthenticationService,
    private val hikerFacade: HikerFacade,
    private val clock: Clock,
) : UpdateHiker {
    override fun updateHikerInformation(query: UpdateHikerInformationQuery) {
        authenticationService.requireAuthenticated(query.requesterId)
        if (query.requesterId != query.hikerId) throw UnauthorizedException()
        hikerFacade.updateInformation(
            hikerId = query.hikerId,
            personalInformation = PersonalInformation(query.firstName, query.lastName, query.phoneNumber, query.homeAddress),
            emergencyContact = EmergencyContact(query.emergencyContact.name, query.emergencyContact.phoneNumber),
            skillLevel = query.skillLevel,
            now = LocalDateTime.now(clock),
        )
    }

    override fun updateMembershipStatus(query: UpdateMembershipStatusQuery) {
        authenticationService.requireAdministrator(query.administratorId)
        hikerFacade.changeMembershipStatus(query.hikerId, query.membershipStatus, query.administratorId, LocalDateTime.now(clock))
    }

    override fun recordLeaderCertification(query: RecordLeaderCertificationQuery) {
        authenticationService.requireAdministrator(query.administratorId)
        hikerFacade.recordLeaderCertification(
            hikerId = query.hikerId,
            certificateNumber = query.certificateNumber,
            issueDate = query.issueDate,
            expiryDate = query.expiryDate,
            organizerLevel = query.organizerLevel,
            administratorId = query.administratorId,
            now = LocalDateTime.now(clock),
        )
    }
}
