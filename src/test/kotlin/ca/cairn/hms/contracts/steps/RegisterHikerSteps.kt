package ca.cairn.hms.contracts.steps

import ca.cairn.hms.application.dtos.queries.EmergencyContactDto
import ca.cairn.hms.application.dtos.queries.RegisterHikerQuery
import ca.cairn.hms.application.dtos.queries.SignUpForHikeQuery
import ca.cairn.hms.application.usecases.RegisterHiker
import ca.cairn.hms.application.usecases.SignUpForHike
import ca.cairn.hms.contracts.support.French
import ca.cairn.hms.contracts.support.ScenarioContext
import ca.cairn.hms.contracts.support.TestData
import ca.cairn.hms.domain.hiker.Hiker
import ca.cairn.hms.domain.hiker.HikerRegistered
import ca.cairn.hms.domain.hiker.SkillLevel
import ca.cairn.hms.domain.signup.SignUpStatus
import ca.cairn.hms.stubs.AuthenticationServiceStub
import ca.cairn.hms.stubs.ClockStub
import ca.cairn.hms.stubs.DomainEventEmitterStub
import ca.cairn.hms.stubs.HikerRepositoryStub
import ca.cairn.hms.stubs.InvitationCodeServiceStub
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue

class RegisterHikerSteps(
    private val world: ScenarioContext,
    private val data: TestData,
    private val clock: ClockStub,
    private val hikers: HikerRepositoryStub,
    private val invitations: InvitationCodeServiceStub,
    private val authentication: AuthenticationServiceStub,
    private val events: DomainEventEmitterStub,
    private val registerHiker: RegisterHiker,
    private val signUpForHike: SignUpForHike,
) {
    @Given("""^un code d'invitation valide "([^"]*)"$""")
    fun validInvitationCode(code: String) {
        invitations.validCodes += code
    }

    /** Les détails facultatifs (courriel, âge, décharge, tuteur, personne-ressource) sont lus dans la suite de la phrase. */
    @When("""^un visiteur s'enregistre avec le code "([^"]*)"(.*)$""")
    fun visitorRegisters(
        code: String,
        details: String,
    ) {
        val email = Regex("le courriel \"([^\"]*)\"").find(details)?.groupValues?.get(1) ?: "membre@exemple.ca"
        val age = Regex("donnant (\\d+) ans").find(details)?.groupValues?.get(1)?.toLong() ?: 30L
        val guardian = Regex("le tuteur légal \"([^\"]*)\"").find(details)?.groupValues?.get(1)
        val waiverAccepted = !details.contains("sans accepter la décharge")
        val emergencyContact =
            if (details.contains("sans personne-ressource")) EmergencyContactDto("", "") else EmergencyContactDto("Paul Tremblay", "613-555-0102")

        world.subjectEmail = email
        world.countBefore = hikers.count()
        world.attempt {
            world.registeredHikerId =
                registerHiker.registerHiker(
                    RegisterHikerQuery(
                        invitationCode = code,
                        email = email,
                        password = "MotDePasse-2026",
                        firstName = "Ana",
                        lastName = "Roy",
                        phoneNumber = "613-555-0100",
                        homeAddress = "1 rue Principale, Ottawa",
                        dateOfBirth = clock.today().minusYears(age),
                        emergencyContact = emergencyContact,
                        skillLevel = SkillLevel.BEGINNER,
                        waiverAccepted = waiverAccepted,
                        legalGuardianName = guardian,
                    ),
                )
        }
    }

    private fun registeredHiker(): Hiker {
        assertNull(world.error, "Enregistrement refusé : ${world.error?.message}")
        return hikers.findById(world.registeredHikerId!!)!!
    }

    @Then("""^un randonneur identifié par "([^"]*)" est créé$""")
    fun hikerIdentifiedByEmailCreated(email: String) {
        val hiker = registeredHiker()
        assertEquals(hiker.id, hikers.findByEmail(email)!!.id)
        assertTrue(events.events.any { it is HikerRegistered && it.hikerId == hiker.id })
        assertNotNull(authentication.credentials[hiker.id], "Identifiants de connexion créés")
    }

    @Then("""^un randonneur est créé avec une adhésion "([^"]*)"$""")
    fun hikerCreatedWithMembership(membership: String) = membershipIs(membership)

    @Then("""^son adhésion est "([^"]*)"$""")
    fun membershipIs(membership: String) {
        assertEquals(French.membershipStatus(membership), registeredHiker().membershipStatus)
    }

    @Then("""^il peut s'inscrire à des randonnées$""")
    fun canSignUpForHikes() {
        val hiker = registeredHiker()
        authentication.logIn(hiker.id)
        val hike = data.hike("Randonnée d'accueil")
        assertEquals(SignUpStatus.CONFIRMED, signUpForHike.signUpForHike(SignUpForHikeQuery(hiker.id, hike.id)).status)
    }

    @Then("""^le nom du tuteur légal "([^"]*)" est consigné$""")
    fun legalGuardianRecorded(name: String) {
        assertEquals(name, registeredHiker().waiver.legalGuardianName)
    }

    @Then("""^aucun randonneur n'est créé$""")
    fun noHikerCreated() {
        assertNull(world.registeredHikerId)
        assertEquals(world.countBefore, hikers.count())
    }
}
