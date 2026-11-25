package ca.cairn.hms.contracts.steps

import ca.cairn.hms.contracts.support.French
import ca.cairn.hms.contracts.support.ScenarioContext
import ca.cairn.hms.contracts.support.TestData
import ca.cairn.hms.domain.hike.HikeBecameFull
import ca.cairn.hms.domain.hike.HikeCancelled
import ca.cairn.hms.domain.hike.HikeClosed
import ca.cairn.hms.domain.hike.HikeCreated
import ca.cairn.hms.domain.hike.HikeReopened
import ca.cairn.hms.domain.hike.HikeStatus
import ca.cairn.hms.domain.shared.Actor
import ca.cairn.hms.domain.shared.BusinessRuleViolationException
import ca.cairn.hms.domain.shared.DomainEvent
import ca.cairn.hms.domain.shared.UnauthenticatedException
import ca.cairn.hms.domain.shared.UnauthorizedException
import ca.cairn.hms.domain.signup.SignUpFacade
import ca.cairn.hms.stubs.DomainEventEmitterStub
import ca.cairn.hms.stubs.EmailServiceStub
import ca.cairn.hms.stubs.HikeRepositoryStub
import io.cucumber.datatable.DataTable
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertTrue
import java.util.UUID

/** Étapes partagées par plusieurs contrats : refus, statut et journal des randonnées, inscriptions, courriels. */
class CommonSteps(
    private val world: ScenarioContext,
    private val data: TestData,
    private val hikes: HikeRepositoryStub,
    private val signUpFacade: SignUpFacade,
    private val events: DomainEventEmitterStub,
    private val emails: EmailServiceStub,
) {
    // ---------------------------------------------------------------- refus

    @Then("""^(?:l'enregistrement|l'inscription|la création|l'annulation|la fermeture|la mise à jour) est refusée? avec le motif "([^"]*)"$""")
    fun refusedWithReason(reason: String) {
        val error = world.error
        assertInstanceOf(BusinessRuleViolationException::class.java, error, "La commande aurait dû être refusée avec le motif « $reason »")
        assertEquals(reason, (error as BusinessRuleViolationException).reason)
    }

    @Then("""^(?:l'enregistrement|l'inscription|la création|l'annulation|la mise à jour) est refusée? pour défaut d'autorisation$""")
    fun refusedForAuthorization() {
        assertInstanceOf(UnauthorizedException::class.java, world.error, "La commande aurait dû être refusée pour défaut d'autorisation")
    }

    @Then("""^la commande est refusée pour défaut d'authentification$""")
    fun refusedForAuthentication() {
        assertInstanceOf(UnauthenticatedException::class.java, world.error, "La commande aurait dû être refusée pour défaut d'authentification")
    }

    // ---------------------------------------------------------------- randonnées

    @Then("""^la randonnée "([^"]*)" (?:est|reste|devient|redevient) "([^"]*)"(?: avec (\d+) places? restantes?)?$""")
    fun hikeHasStatus(
        title: String,
        status: String,
        remainingSpots: String?,
    ) {
        val hike = hikes.findById(world.hikeId(title))!!
        assertEquals(French.hikeStatus(status), hike.status, "Statut de la randonnée « $title »")
        remainingSpots?.let { assertEquals(it.toInt(), signUpFacade.remainingSpots(hike), "Places restantes") }
    }

    @Then("""^la randonnée "([^"]*)" a (\d+) places? restantes?$""")
    fun hikeHasRemainingSpots(
        title: String,
        remainingSpots: Int,
    ) {
        assertEquals(remainingSpots, signUpFacade.remainingSpots(hikes.findById(world.hikeId(title))!!))
    }

    @Then("""^l'étape "([^"]*)" est consignée avec l'utilisateur "([^"]*)"$""")
    fun transitionRecordedWithUser(
        status: String,
        email: String,
    ) = assertTransitionRecorded(French.hikeStatus(status), Actor.User(world.userId(email)))

    @Then("""^l'étape "([^"]*)" est consignée comme effectuée par le système$""")
    fun transitionRecordedBySystem(status: String) = assertTransitionRecorded(French.hikeStatus(status), Actor.System)

    private fun assertTransitionRecorded(
        status: HikeStatus,
        actor: Actor,
    ) {
        val hikeId = world.currentHikeId!!
        val last = hikes.findById(hikeId)!!.statusHistory.last()
        assertEquals(status, last.status, "Dernière étape consignée")
        assertEquals(actor, last.changedBy, "Auteur de l'étape")
        assertTrue(events.events.any { it.isTransitionEvent(status, hikeId) }, "Événement de domaine émis pour l'étape $status")
    }

    private fun DomainEvent.isTransitionEvent(
        status: HikeStatus,
        hikeId: UUID,
    ) = when (this) {
        is HikeCreated -> status == HikeStatus.OPEN && this.hikeId == hikeId
        is HikeReopened -> status == HikeStatus.OPEN && this.hikeId == hikeId
        is HikeBecameFull -> status == HikeStatus.FULL && this.hikeId == hikeId
        is HikeClosed -> status == HikeStatus.CLOSED && this.hikeId == hikeId
        is HikeCancelled -> status == HikeStatus.CANCELLED && this.hikeId == hikeId
        else -> false
    }

    // ---------------------------------------------------------------- inscriptions et courriels

    @Given("""^les inscriptions suivantes à "([^"]*)" :$""")
    fun existingSignUps(
        title: String,
        table: DataTable,
    ) {
        val hikeId = world.hikeId(title)
        table.asMaps().forEach { data.signUp(hikeId, it["randonneur"]!!, French.signUpStatus(it["statut"]!!)) }
    }

    @Then("""^les inscriptions de "([^"]*)" et "([^"]*)" sont "([^"]*)"$""")
    fun signUpsHaveStatus(
        first: String,
        second: String,
        status: String,
    ) {
        listOf(first, second).forEach {
            assertEquals(French.signUpStatus(status), data.signUpOf(it, world.currentHikeId!!)!!.status, "Inscription de $it")
        }
    }

    @Then("""^aucun courriel n'est envoyé à "([^"]*)"$""")
    fun noEmailSentTo(email: String) {
        assertTrue(emails.sentTo(email).isEmpty(), "Aucun courriel attendu pour $email")
    }
}
