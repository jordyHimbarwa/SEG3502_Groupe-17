package ca.cairn.hms.contracts.steps

import ca.cairn.hms.application.dtos.queries.CreateHikeQuery
import ca.cairn.hms.application.dtos.queries.SignUpForHikeQuery
import ca.cairn.hms.application.usecases.CreateHike
import ca.cairn.hms.application.usecases.SignUpForHike
import ca.cairn.hms.contracts.support.French
import ca.cairn.hms.contracts.support.ScenarioContext
import ca.cairn.hms.contracts.support.TestData
import ca.cairn.hms.domain.hike.HikeCreated
import ca.cairn.hms.domain.hiker.SkillLevel
import ca.cairn.hms.domain.signup.SignUpStatus
import ca.cairn.hms.stubs.CcnTrailServiceStub
import ca.cairn.hms.stubs.DomainEventEmitterStub
import ca.cairn.hms.stubs.HikeRepositoryStub
import ca.cairn.hms.stubs.TrailRepositoryStub
import io.cucumber.datatable.DataTable
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import java.time.Duration
import java.time.LocalDate

class CreateHikeSteps(
    private val world: ScenarioContext,
    private val data: TestData,
    private val ccn: CcnTrailServiceStub,
    private val localTrails: TrailRepositoryStub,
    private val hikes: HikeRepositoryStub,
    private val events: DomainEventEmitterStub,
    private val createHike: CreateHike,
    private val signUpForHike: SignUpForHike,
) {
    @Given("""^les sentiers de la CCN suivants :$""")
    fun ccnTrails(table: DataTable) {
        table.asMaps().forEach {
            val id = it["identifiant"]!!
            ccn.trails[id] =
                data.trail(
                    id = id,
                    name = it["nom"]!!,
                    difficulty = French.difficulty(it["difficulté"]!!),
                    season = French.season(it["saison"]!!),
                    hikingAllowed = French.yesNo(it["randonnée permise"]!!),
                )
        }
    }

    @Given("""^un organisateur connecté "([^"]*)" de niveau d'organisateur "([^"]*)"(?: dont la certification expire le "([^"]*)")?$""")
    fun loggedInOrganizer(
        email: String,
        organizerLevel: String,
        expiry: String?,
    ) {
        if (expiry != null) {
            data.organizer(email, French.difficulty(organizerLevel), LocalDate.parse(expiry))
        } else {
            data.organizer(email, French.difficulty(organizerLevel))
        }
    }

    @Given("""^(?:que )?le service de la CCN est indisponible$""")
    fun ccnUnavailable() {
        ccn.available = false
    }

    @Given("""^(?:que )?le sentier "([^"]*)" figure dans la copie locale des sentiers de la CCN$""")
    fun trailInLocalCopy(trailId: String) {
        localTrails.save(ccn.trails[trailId]!!)
    }

    @When(
        """^"([^"]*)" crée la randonnée "([^"]*)" sur le sentier "([^"]*)" le "([^"]*)" à (\d+) h, d'une durée de (\d+) h, """ +
            """avec une date limite d'inscription le "([^"]*)" et une capacité de (\d+)$""",
    )
    fun createFullySpecifiedHike(
        email: String,
        title: String,
        trailId: String,
        date: String,
        hour: Int,
        durationHours: Int,
        deadline: String,
        capacity: Int,
    ) = create(email, trailId, LocalDate.parse(date), LocalDate.parse(deadline), capacity, title, hour, durationHours)

    @When("""^"([^"]*)" crée une randonnée sur le sentier "([^"]*)" le "([^"]*)" avec une date limite d'inscription le "([^"]*)"(?: et une capacité de (-?\d+))?$""")
    fun createHikeOnDate(
        email: String,
        trailId: String,
        date: String,
        deadline: String,
        capacity: String?,
    ) = create(email, trailId, LocalDate.parse(date), LocalDate.parse(deadline), capacity?.toInt() ?: DEFAULT_CAPACITY)

    @When("""^"([^"]*)" crée une randonnée(?: valide)? sur le sentier "([^"]*)"$""")
    fun createValidHike(
        email: String,
        trailId: String,
    ) = create(email, trailId, DEFAULT_DATE, DEFAULT_DATE.minusDays(7), DEFAULT_CAPACITY)

    @Suppress("LongParameterList")
    private fun create(
        email: String,
        trailId: String,
        date: LocalDate,
        deadline: LocalDate,
        capacity: Int,
        title: String = "Randonnée de test",
        hour: Int = 9,
        durationHours: Int = 3,
    ) {
        world.countBefore = hikes.count()
        world.attempt {
            val id =
                createHike.createHike(
                    CreateHikeQuery(
                        organizerId = world.userId(email),
                        title = title,
                        description = "Description de $title",
                        trailId = trailId,
                        startDateTime = date.atTime(hour, 0),
                        expectedDuration = Duration.ofHours(durationHours.toLong()),
                        signUpDeadline = deadline,
                        capacity = capacity,
                    ),
                )
            world.createdHikeId = id
            world.currentHikeId = id
        }
    }

    @Then("""^la randonnée reçoit un identifiant unique$""")
    fun hikeHasUniqueId() {
        assertNull(world.error, "Création refusée : ${world.error?.message}")
        val id = world.createdHikeId
        assertNotNull(id)
        assertNotNull(hikes.findById(id!!))
        assertEquals(1, hikes.findAll().count { it.id == id })
        assertTrue(events.events.any { it is HikeCreated && it.hikeId == id }, "Événement HikeCreated émis")
    }

    @Then("""^la randonnée est "([^"]*)" avec la difficulté "([^"]*)"$""")
    fun createdHikeHasStatusAndDifficulty(
        status: String,
        difficulty: String,
    ) {
        val hike = hikes.findById(world.createdHikeId!!)!!
        assertEquals(French.hikeStatus(status), hike.status)
        assertEquals(French.difficulty(difficulty), hike.difficulty)
    }

    @Then("""^les randonneurs peuvent s'y inscrire$""")
    fun hikersCanSignUp() {
        val hiker = data.hiker("premier-inscrit@exemple.ca", SkillLevel.EXPERT)
        val result = signUpForHike.signUpForHike(SignUpForHikeQuery(hiker.id, world.createdHikeId!!))
        assertEquals(SignUpStatus.CONFIRMED, result.status)
    }

    @Then("""^aucune randonnée n'est créée$""")
    fun noHikeCreated() {
        assertNull(world.createdHikeId)
        assertEquals(world.countBefore, hikes.count())
        assertFalse(events.events.any { it is HikeCreated })
    }

    @Then("""^la randonnée est créée à partir de la copie locale des sentiers$""")
    fun hikeCreatedFromLocalCopy() {
        assertNull(world.error, "Création refusée : ${world.error?.message}")
        assertFalse(ccn.available)
        assertTrue(ccn.calls > 0, "La CCN aurait dû être consultée en premier")
        val hike = hikes.findById(world.createdHikeId!!)!!
        assertEquals(localTrails.findById(hike.trailId)!!.difficulty, hike.difficulty)
    }

    companion object {
        private val DEFAULT_DATE: LocalDate = LocalDate.of(2026, 11, 8)
        private const val DEFAULT_CAPACITY = 12
    }
}
