package ca.cairn.hms.domain.hiker

import java.time.LocalDate
import java.time.Period
import java.util.UUID

/** Entité racine de l'agrégat Randonneur. Le courriel identifie le randonneur de façon unique. */
class Hiker(
    val id: UUID,
    val email: String,
    personalInformation: PersonalInformation,
    val dateOfBirth: LocalDate,
    emergencyContact: EmergencyContact,
    skillLevel: SkillLevel,
    val waiver: Waiver,
    membershipStatus: MembershipStatus = MembershipStatus.ACTIVE,
    leaderCertification: LeaderCertification? = null,
) {
    var personalInformation: PersonalInformation = personalInformation
        private set
    var emergencyContact: EmergencyContact = emergencyContact
        private set
    var skillLevel: SkillLevel = skillLevel
        private set
    var membershipStatus: MembershipStatus = membershipStatus
        private set
    var leaderCertification: LeaderCertification? = leaderCertification
        private set

    val fullName: String get() = "${personalInformation.firstName} ${personalInformation.lastName}"

    val isOrganizer: Boolean get() = leaderCertification != null

    fun hasActiveMembership(): Boolean = membershipStatus == MembershipStatus.ACTIVE

    fun ageOn(date: LocalDate): Int = Period.between(dateOfBirth, date).years

    fun updateInformation(
        personalInformation: PersonalInformation,
        emergencyContact: EmergencyContact,
        skillLevel: SkillLevel,
    ) {
        this.personalInformation = personalInformation
        this.emergencyContact = emergencyContact
        this.skillLevel = skillLevel
    }

    fun changeMembershipStatus(status: MembershipStatus) {
        membershipStatus = status
    }

    fun recordLeaderCertification(certification: LeaderCertification) {
        leaderCertification = certification
    }

    companion object {
        const val AGE_OF_MAJORITY = 18
    }
}
