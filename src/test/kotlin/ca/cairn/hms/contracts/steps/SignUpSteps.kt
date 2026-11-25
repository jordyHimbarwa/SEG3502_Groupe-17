package ca.cairn.hms.contracts.steps

import ca.cairn.hms.application.dtos.queries.CancelSignUpQuery
import ca.cairn.hms.application.dtos.queries.SignUpForHikeQuery
import ca.cairn.hms.application.usecases.CancelSignUp
import ca.cairn.hms.application.usecases.SignUpForHike
import ca.cairn.hms.application.usecases.implementation.EmailMessages
import ca.cairn.hms.contracts.support.French
import ca.cairn.hms.contracts.support.ScenarioContext
import ca.cairn.hms.contracts.support.TestData
import ca.cairn.hms.domain.hike.HikeStatus
import ca.cairn.hms.domain.signup.HikerSignedUp
import ca.cairn.hms.domain.signup.HikerWaitlisted
import ca.cairn.hms.domain.signup.SignUpFacade
import ca.cairn.hms.domain.signup.SignUpStatus
import ca.cairn.hms.stubs.ClockStub
import ca.cairn.hms.stubs.DomainEventEmitterStub
import ca.cairn.hms.stubs.EmailServiceStub
import ca.cairn.hms.stubs.HikerRepositoryStub
import ca.cairn.hms.stubs.SignUpRepositoryStub
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue

/** Contrats S'inscrire à une randonnée et Annuler une inscription. */
class SignUpSteps(
    private val world: ScenarioContext,
    private val data: TestData,
    private val clock: ClockStub,
    private val hikers: HikerRepositoryStub,
    private val signUps: SignUpRepositoryStub,
    private val signUpFacade: SignUpFacade,
    private val emails: EmailServiceStub,
    private val events: DomainEventEmitterStub,
    private val signUpForHike: SignUpForHike,
    private val cancelSignUp: CancelSignUp,
) {
    // ---------------------------------------------------------------- Étant donné

    @Given(
        """^une randonnée "([^"]*)" de difficulté "([^"]*)", (ouverte|complète|fermée|annulée)""" +
            """(?:, de capacité (\d+) avec (\d+) inscriptions confirmées)?$""",
    )
    fun hikeWithDifficulty(
        title: String,
        difficulty: String,
        status: String,
        capacity: String?,
        confirmed: String?,
    ) {
        val hikeStatus = French.hikeStatus(status)
        val hikeCapacity = capacity?.toInt() ?: defaultCapacity(hikeStatus)
        val hike = data.hike(title, French.difficulty(difficulty), hikeStatus, hikeCapacity)
        data.confirmedParticipants(hike.id, confirmed?.toInt() ?: if (hikeStatus == HikeStatus.FULL) hikeCapacity else 0)
    }

    @Given("""^une randonnée "([^"]*)" de difficulté "([^"]*)", complète, avec (\d+) inscriptions en liste d'attente$""")
    fun fullHikeWithWaitlist(
        title: String,
        difficulty: String,
        waitlisted: Int,
    ) {
        hikeWithDifficulty(title, difficulty, "complète", null, null)
        data.waitlistedParticipants(world.hikeId(title), waitlisted)
    }

    @Given("""^une adhésion "([^"]*)" et une randonnée "([^"]*)" de difficulté "([^"]*)", (ouverte|complète|fermée|annulée)$""")
    fun membershipAndHike(
        membership: String,
        title: String,
        difficulty: String,
        status: String,
    ) {
        val hiker = hikers.findById(world.userId(world.currentHikerEmail!!))!!
        hiker.changeMembershipStatus(French.membershipStatus(membership))
        hikers.save(hiker)
        hikeWithDifficulty(title, difficulty, status, null, null)
    }

    @Given("""^une randonnée "([^"]*)" ouverte, de capacité (\d+) avec (\d+) inscriptions confirmées dont celle de "([^"]*)"$""")
    fun openHikeIncludingSignUpOf(
        title: String,
        capacity: Int,
        confirmed: Int,
        email: String,
    ) {
        val hike = data.hike(title, capacity = capacity)
        data.signUp(hike.id, email, SignUpStatus.CONFIRMED)
        data.confirmedParticipants(hike.id, confirmed - 1)
    }

    @Given("""^une randonnée "([^"]*)" (ouverte|complète|fermée|annulée) dont "([^"]*)" a une inscription confirmée$""")
    fun hikeWithConfirmedSignUpOf(
        title: String,
        status: String,
        email: String,
    ) {
        val hikeStatus = French.hikeStatus(status)
        val capacity = defaultCapacity(hikeStatus)
        val hike = data.hike(title, status = hikeStatus, capacity = capacity)
        data.signUp(hike.id, email, SignUpStatus.CONFIRMED)
        if (hikeStatus == HikeStatus.FULL) data.confirmedParticipants(hike.id, capacity - 1)
    }

    @Given("""^une randonnée "([^"]*)" (ouverte|complète|fermée|annulée)$""")
    fun hikeWithStatus(
        title: String,
        status: String,
    ) {
        val hikeStatus = French.hikeStatus(status)
        val capacity = defaultCapacity(hikeStatus)
        val hike = data.hike(title, status = hikeStatus, capacity = capacity)
        if (hikeStatus == HikeStatus.FULL) data.confirmedParticipants(hike.id, capacity)
    }

    @Given("""^la liste d'attente de "([^"]*)" contient dans l'ordre (.+)$""")
    fun waitlistContains(
        title: String,
        hikers: String,
    ) {
        French.quotedValues(hikers).forEach { data.signUp(world.hikeId(title), it, SignUpStatus.WAITLISTED) }
    }

    @Given("""^la liste d'attente de "([^"]*)" est vide$""")
    fun waitlistIsEmpty(title: String) {
        assertTrue(signUpFacade.waitlist(world.hikeId(title)).isEmpty())
    }

    @Given("""^"([^"]*)" a déjà une inscription "([^"]*)" à "([^"]*)"$""")
    fun existingSignUp(
        email: String,
        status: String,
        title: String,
    ) {
        data.signUp(world.hikeId(title), email, French.signUpStatus(status))
    }

    private fun defaultCapacity(status: HikeStatus) = if (status == HikeStatus.FULL) FULL_HIKE_CAPACITY else DEFAULT_CAPACITY

    // ---------------------------------------------------------------- Quand

    @When("""^"([^"]*)" s'inscrit à "([^"]*)"$""")
    fun signUp(
        email: String,
        title: String,
    ) {
        world.subjectEmail = email
        world.currentHikeId = world.hikeId(title)
        world.attempt {
            val result = signUpForHike.signUpForHike(SignUpForHikeQuery(world.userId(email), world.hikeId(title)))
            world.signUpResult = result
            world.lastSignUpId = result.signUpId
        }
    }

    @When("""^"([^"]*)" annule son inscription à "([^"]*)"$""")
    fun cancelOwnSignUp(
        email: String,
        title: String,
    ) = cancel(email, email, title)

    @When("""^"([^"]*)" annule l'inscription de "([^"]*)" à "([^"]*)"$""")
    fun cancelSignUpOfAnother(
        requester: String,
        owner: String,
        title: String,
    ) = cancel(requester, owner, title)

    private fun cancel(
        requesterEmail: String,
        ownerEmail: String,
        title: String,
    ) {
        val requesterId = data.existingOrNewHiker(requesterEmail).id
        val signUp = data.signUpOf(ownerEmail, world.hikeId(title))!!
        world.subjectEmail = ownerEmail
        world.currentHikeId = world.hikeId(title)
        world.lastSignUpId = signUp.id
        world.attempt { cancelSignUp.cancelSignUp(CancelSignUpQuery(requesterId, signUp.id)) }
    }

    // ---------------------------------------------------------------- Alors

    @Then("""^son inscription (?:est|reste) "([^"]*)"( avec l'heure à laquelle elle a été faite)?(?: en position (\d+))?$""")
    fun ownSignUpHasStatus(
        status: String,
        withTime: String?,
        position: String?,
    ) {
        val signUp = signUps.findById(world.lastSignUpId ?: error("Aucune inscription : ${world.error?.message}"))!!
        assertEquals(French.signUpStatus(status), signUp.status, "Statut de l'inscription")
        if (withTime != null) assertEquals(clock.now(), signUp.signedUpAt, "Heure de l'inscription")
        if (position != null) {
            assertEquals(position.toInt(), signUpFacade.waitlistPosition(signUp))
            assertEquals(position.toInt(), world.signUpResult!!.waitlistPosition)
            assertTrue(events.events.any { it is HikerWaitlisted && it.signUpId == signUp.id })
        }
        if (withTime != null) assertTrue(events.events.any { it is HikerSignedUp && it.signUpId == signUp.id })
    }

    @Then("""^l'inscription de "([^"]*)" devient "([^"]*)"$""")
    fun signUpOfBecomes(
        email: String,
        status: String,
    ) {
        assertEquals(French.signUpStatus(status), data.signUpOf(email, world.currentHikeId!!)!!.status)
    }

    @Then("""^"([^"]*)" est en position (\d+) de la liste d'attente$""")
    fun waitlistPosition(
        email: String,
        position: Int,
    ) {
        assertEquals(position, signUpFacade.waitlistPosition(data.signUpOf(email, world.currentHikeId!!)!!))
    }

    @Then("""^un courriel de confirmation est envoyé à "([^"]*)"$""")
    fun confirmationEmailSent(email: String) {
        assertTrue(emails.sentTo(email).any { it.subject == EmailMessages.SIGN_UP_CONFIRMED_SUBJECT })
    }

    @Then("""^un courriel indiquant la position (\d+) dans la liste d'attente est envoyé à "([^"]*)"$""")
    fun waitlistEmailSent(
        position: Int,
        email: String,
    ) {
        assertTrue(
            emails.sentTo(email).any { it.subject == EmailMessages.WAITLISTED_SUBJECT && it.body.contains("position $position") },
            "Courriels reçus : ${emails.sentTo(email)}",
        )
    }

    @Then("""^un courriel l'informant de son inscription est envoyé à "([^"]*)"$""")
    fun promotionEmailSent(email: String) {
        assertTrue(emails.sentTo(email).any { it.subject == EmailMessages.PROMOTED_SUBJECT })
    }

    @Then("""^aucune inscription n'est créée$""")
    fun noSignUpCreated() {
        assertNull(world.signUpResult)
        assertNull(data.signUpOf(world.subjectEmail!!, world.currentHikeId!!))
    }

    companion object {
        private const val DEFAULT_CAPACITY = 10
        private const val FULL_HIKE_CAPACITY = 5
    }
}
