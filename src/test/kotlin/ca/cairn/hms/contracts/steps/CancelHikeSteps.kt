package ca.cairn.hms.contracts.steps

import ca.cairn.hms.application.dtos.queries.CancelHikeQuery
import ca.cairn.hms.application.usecases.CancelHike
import ca.cairn.hms.application.usecases.implementation.EmailMessages
import ca.cairn.hms.contracts.support.ScenarioContext
import ca.cairn.hms.contracts.support.TestData
import ca.cairn.hms.stubs.EmailServiceStub
import ca.cairn.hms.stubs.HikeRepositoryStub
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

class CancelHikeSteps(
    private val world: ScenarioContext,
    private val data: TestData,
    private val hikes: HikeRepositoryStub,
    private val emails: EmailServiceStub,
    private val cancelHike: CancelHike,
) {
    @Given("""^une randonnée "([^"]*)" organisée par "([^"]*)" qui n'a pas commencé$""")
    fun hikeOrganizedBy(
        title: String,
        organizerEmail: String,
    ) {
        data.hike(title, organizerEmail = organizerEmail)
    }

    @Given("""^(?:que |qu')?"([^"]*)" est connecté en tant (?:que |qu')(.+)$""")
    fun loggedInAs(
        email: String,
        role: String,
    ) {
        when (role.trim()) {
            "administrateur" -> data.administrator(email)
            "organisateur de la randonnée" ->
                assertEquals(world.userId(email), hikes.findById(world.currentHikeId!!)!!.organizerId, "$email organise la randonnée")
            "organisateur d'autres randonnées" -> data.organizer(email)
            else -> error("Rôle inconnu : $role")
        }
    }

    @Given("""^(?:que )?la randonnée "([^"]*)" a commencé$""")
    fun hikeHasStarted(title: String) {
        data.moveClockTo(hikes.findById(world.hikeId(title))!!.schedule.startDateTime.plusHours(1))
    }

    @When("""^"([^"]*)" annule la randonnée "([^"]*)" pour le motif "([^"]*)"$""")
    fun cancelsHike(
        email: String,
        title: String,
        reason: String,
    ) = cancel(email, title, reason)

    @When("""^"([^"]*)" annule la randonnée "([^"]*)" sans motif$""")
    fun cancelsHikeWithoutReason(
        email: String,
        title: String,
    ) = cancel(email, title, "")

    private fun cancel(
        email: String,
        title: String,
        reason: String,
    ) {
        val requesterId = world.userIds[email] ?: data.existingOrNewHiker(email).id
        world.currentHikeId = world.hikeId(title)
        world.attempt { cancelHike.cancelHike(CancelHikeQuery(requesterId, world.hikeId(title), reason)) }
    }

    @Then("""^un courriel contenant le motif "([^"]*)" est envoyé à "([^"]*)" et "([^"]*)"$""")
    fun cancellationEmailsSent(
        reason: String,
        first: String,
        second: String,
    ) {
        listOf(first, second).forEach { email ->
            assertTrue(
                emails.sentTo(email).any { it.subject == EmailMessages.HIKE_CANCELLED_SUBJECT && it.body.contains(reason) },
                "Courriel d'annulation avec le motif attendu pour $email",
            )
        }
    }
}
