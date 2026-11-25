package ca.cairn.hms.contracts.steps

import ca.cairn.hms.application.dtos.queries.EmergencyContactDto
import ca.cairn.hms.application.dtos.queries.RecordLeaderCertificationQuery
import ca.cairn.hms.application.dtos.queries.UpdateHikerInformationQuery
import ca.cairn.hms.application.dtos.queries.UpdateMembershipStatusQuery
import ca.cairn.hms.application.usecases.UpdateHiker
import ca.cairn.hms.contracts.support.French
import ca.cairn.hms.contracts.support.ScenarioContext
import ca.cairn.hms.contracts.support.TestData
import ca.cairn.hms.domain.hiker.Hiker
import ca.cairn.hms.domain.hiker.LeaderCertificationRecorded
import ca.cairn.hms.domain.hiker.MembershipStatusChanged
import ca.cairn.hms.domain.trail.TrailDifficulty
import ca.cairn.hms.stubs.DomainEventEmitterStub
import ca.cairn.hms.stubs.HikerRepositoryStub
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import java.time.LocalDate

/** Contrats de Mettre à jour un randonneur : informations, statut d'adhésion et certification. */
class UpdateHikerSteps(
    private val world: ScenarioContext,
    private val data: TestData,
    private val hikers: HikerRepositoryStub,
    private val events: DomainEventEmitterStub,
    private val updateHiker: UpdateHiker,
) {
    private fun hiker(email: String): Hiker = hikers.findById(world.userId(email))!!

    private fun subject(): Hiker = hiker(world.subjectEmail!!)

    // ---------------------------------------------------------------- informations

    @When("""^"([^"]*)" soumet ses informations avec le téléphone "([^"]*)" et le niveau "([^"]*)"$""")
    fun submitsInformation(
        email: String,
        phone: String,
        skillLevel: String,
    ) = submit(email, email, phone, French.skillLevel(skillLevel))

    @When("""^"([^"]*)" soumet ses informations sans numéro de téléphone$""")
    fun submitsWithoutPhone(email: String) = submit(email, email, phone = "")

    @When("""^"([^"]*)" soumet des informations pour "([^"]*)"$""")
    fun submitsForAnother(
        requester: String,
        target: String,
    ) = submit(requester, target)

    private fun submit(
        requesterEmail: String,
        targetEmail: String,
        phone: String? = null,
        skillLevel: ca.cairn.hms.domain.hiker.SkillLevel? = null,
    ) {
        val target = hiker(targetEmail)
        world.subjectEmail = targetEmail
        world.snapshot = Triple(target.personalInformation, target.emergencyContact, target.skillLevel)
        val info = target.personalInformation
        world.attempt {
            updateHiker.updateHikerInformation(
                UpdateHikerInformationQuery(
                    requesterId = world.userId(requesterEmail),
                    hikerId = target.id,
                    firstName = info.firstName,
                    lastName = info.lastName,
                    phoneNumber = phone ?: info.phoneNumber,
                    homeAddress = info.homeAddress,
                    emergencyContact = EmergencyContactDto(target.emergencyContact.name, target.emergencyContact.phoneNumber),
                    skillLevel = skillLevel ?: target.skillLevel,
                ),
            )
        }
    }

    @Then("""^le téléphone de "([^"]*)" est "([^"]*)"$""")
    fun phoneIs(
        email: String,
        phone: String,
    ) {
        assertNull(world.error, "Mise à jour refusée : ${world.error?.message}")
        assertEquals(phone, hiker(email).personalInformation.phoneNumber)
    }

    @Then("""^son niveau d'habileté est "([^"]*)"$""")
    fun skillLevelIs(skillLevel: String) {
        assertEquals(French.skillLevel(skillLevel), subject().skillLevel)
    }

    @Then("""^ses informations restent inchangées$""")
    fun informationUnchanged() {
        val hiker = subject()
        assertEquals(world.snapshot, Triple(hiker.personalInformation, hiker.emergencyContact, hiker.skillLevel))
    }

    // ---------------------------------------------------------------- statut d'adhésion

    @When("""^(?:l'administrateur|"([^"]*)") met le statut d'adhésion de "([^"]*)" à "([^"]*)"$""")
    fun setsMembershipStatus(
        requesterEmail: String?,
        email: String,
        status: String,
    ) {
        val requesterId = requesterEmail?.let(world::userId) ?: world.administratorId!!
        world.subjectEmail = email
        world.attempt {
            updateHiker.updateMembershipStatus(UpdateMembershipStatusQuery(requesterId, world.userId(email), French.membershipStatus(status)))
        }
    }

    @Then("""^l'adhésion de "([^"]*)" est "([^"]*)"$""")
    fun membershipIs(
        email: String,
        status: String,
    ) {
        assertNull(world.error, "Mise à jour refusée : ${world.error?.message}")
        assertEquals(French.membershipStatus(status), hiker(email).membershipStatus)
        assertTrue(events.events.any { it is MembershipStatusChanged && it.hikerId == world.userId(email) })
    }

    // ---------------------------------------------------------------- certification

    @When(
        """^l'administrateur enregistre pour "([^"]*)" le certificat "([^"]*)" délivré le "([^"]*)", expirant le "([^"]*)", """ +
            """au niveau d'organisateur "([^"]*)"$""",
    )
    fun recordsCertification(
        email: String,
        number: String,
        issued: String,
        expires: String,
        level: String,
    ) = record(world.administratorId!!.let { it }, email, number, LocalDate.parse(issued), LocalDate.parse(expires), French.difficulty(level))

    @When("""^l'administrateur enregistre pour "([^"]*)" un certificat délivré le "([^"]*)" expirant le "([^"]*)"$""")
    fun recordsCertificationWithDates(
        email: String,
        issued: String,
        expires: String,
    ) = record(world.administratorId!!, email, "CHR-0001", LocalDate.parse(issued), LocalDate.parse(expires), TrailDifficulty.MODERATE)

    @When("""^"([^"]*)" enregistre une certification pour "([^"]*)"$""")
    fun hikerRecordsCertification(
        requester: String,
        email: String,
    ) = record(world.userId(requester), email, "CHR-0002", LocalDate.of(2026, 5, 1), LocalDate.of(2028, 5, 1), TrailDifficulty.EASY)

    @Suppress("LongParameterList")
    private fun record(
        requesterId: java.util.UUID,
        email: String,
        number: String,
        issued: LocalDate,
        expires: LocalDate,
        level: TrailDifficulty,
    ) {
        world.subjectEmail = email
        world.attempt {
            updateHiker.recordLeaderCertification(
                RecordLeaderCertificationQuery(requesterId, world.userId(email), number, issued, expires, level),
            )
        }
    }

    @Then("""^"([^"]*)" est organisateur de randonnée$""")
    fun isOrganizer(email: String) {
        assertNull(world.error, "Enregistrement refusé : ${world.error?.message}")
        assertTrue(hiker(email).isOrganizer)
        assertTrue(events.events.any { it is LeaderCertificationRecorded && it.hikerId == world.userId(email) })
    }

    @Then("""^"([^"]*)" n'est pas organisateur de randonnée$""")
    fun isNotOrganizer(email: String) {
        assertFalse(hiker(email).isOrganizer)
    }

    @Then("""^sa certification porte le numéro "([^"]*)" et expire le "([^"]*)"$""")
    fun certificationIs(
        number: String,
        expires: String,
    ) {
        val certification = subject().leaderCertification!!
        assertEquals(number, certification.certificateNumber)
        assertEquals(LocalDate.parse(expires), certification.expiryDate)
    }

    @Then("""^son niveau d'organisateur est "([^"]*)"$""")
    fun organizerLevelIs(level: String) {
        assertEquals(French.difficulty(level), subject().leaderCertification!!.organizerLevel)
    }
}
