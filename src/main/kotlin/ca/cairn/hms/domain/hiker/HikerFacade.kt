package ca.cairn.hms.domain.hiker

import ca.cairn.hms.domain.shared.DomainEventEmitter
import ca.cairn.hms.domain.shared.EntityNotFoundException
import ca.cairn.hms.domain.shared.violation
import ca.cairn.hms.domain.trail.TrailDifficulty
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

/** Façade du sous-domaine Randonneurs et adhésions. */
@Service
class HikerFacade(
    private val hikerRepository: HikerRepository,
    private val hikerFactory: HikerFactory,
    private val eventEmitter: DomainEventEmitter,
) {
    fun findHiker(hikerId: UUID): Hiker? = hikerRepository.findById(hikerId)

    fun getHiker(hikerId: UUID): Hiker =
        hikerRepository.findById(hikerId) ?: throw EntityNotFoundException("randonneur introuvable")

    @Suppress("LongParameterList")
    fun registerHiker(
        email: String,
        personalInformation: PersonalInformation,
        dateOfBirth: LocalDate,
        emergencyContact: EmergencyContact,
        skillLevel: SkillLevel,
        waiverAccepted: Boolean,
        legalGuardianName: String?,
        now: LocalDateTime,
    ): Hiker {
        val normalizedEmail = email.trim().lowercase()
        if (!waiverAccepted) violation("décharge non acceptée")
        if (normalizedEmail.isBlank() || !personalInformation.isComplete() || !emergencyContact.isComplete()) {
            violation("informations incomplètes")
        }
        val today = now.toLocalDate()
        val isMinor = today.isBefore(dateOfBirth.plusYears(Hiker.AGE_OF_MAJORITY.toLong()))
        if (isMinor && legalGuardianName.isNullOrBlank()) violation("tuteur légal requis")
        if (hikerRepository.findByEmail(normalizedEmail) != null) violation("courriel déjà utilisé")

        val waiver = Waiver(acceptedOn = today, legalGuardianName = legalGuardianName?.takeIf { it.isNotBlank() })
        val hiker =
            hikerFactory.createHiker(normalizedEmail, personalInformation, dateOfBirth, emergencyContact, skillLevel, waiver)
        hikerRepository.save(hiker)
        eventEmitter.emit(HikerRegistered(hiker.id, hiker.email, now))
        return hiker
    }

    fun updateInformation(
        hikerId: UUID,
        personalInformation: PersonalInformation,
        emergencyContact: EmergencyContact,
        skillLevel: SkillLevel,
        now: LocalDateTime,
    ) {
        val hiker = getHiker(hikerId)
        if (!personalInformation.isComplete() || !emergencyContact.isComplete()) violation("informations incomplètes")
        hiker.updateInformation(personalInformation, emergencyContact, skillLevel)
        hikerRepository.save(hiker)
        eventEmitter.emit(HikerInformationUpdated(hiker.id, now))
    }

    fun changeMembershipStatus(
        hikerId: UUID,
        status: MembershipStatus,
        administratorId: UUID,
        now: LocalDateTime,
    ) {
        val hiker = getHiker(hikerId)
        val previous = hiker.membershipStatus
        hiker.changeMembershipStatus(status)
        hikerRepository.save(hiker)
        eventEmitter.emit(MembershipStatusChanged(hiker.id, previous, status, administratorId, now))
    }

    @Suppress("LongParameterList")
    fun recordLeaderCertification(
        hikerId: UUID,
        certificateNumber: String,
        issueDate: LocalDate,
        expiryDate: LocalDate,
        organizerLevel: TrailDifficulty,
        administratorId: UUID,
        now: LocalDateTime,
    ) {
        val hiker = getHiker(hikerId)
        val certification = LeaderCertification(certificateNumber, issueDate, expiryDate, organizerLevel)
        hiker.recordLeaderCertification(certification)
        hikerRepository.save(hiker)
        eventEmitter.emit(LeaderCertificationRecorded(hiker.id, certificateNumber, organizerLevel, administratorId, now))
    }
}
