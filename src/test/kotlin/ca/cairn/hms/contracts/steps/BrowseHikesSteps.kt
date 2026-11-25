package ca.cairn.hms.contracts.steps

import ca.cairn.hms.application.dtos.queries.ListHikesForSkillLevelQuery
import ca.cairn.hms.application.dtos.queries.SearchOpenHikesQuery
import ca.cairn.hms.application.usecases.BrowseHikes
import ca.cairn.hms.contracts.support.French
import ca.cairn.hms.contracts.support.ScenarioContext
import ca.cairn.hms.contracts.support.TestData
import ca.cairn.hms.domain.hike.HikeStatus
import ca.cairn.hms.domain.trail.TrailDifficulty
import ca.cairn.hms.stubs.HikeRepositoryStub
import io.cucumber.datatable.DataTable
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import java.time.LocalDate

class BrowseHikesSteps(
    private val world: ScenarioContext,
    private val data: TestData,
    private val hikes: HikeRepositoryStub,
    private val browseHikes: BrowseHikes,
) {
    @Given("""^les randonnées (?:ouvertes )?suivantes :$""")
    fun hikes(table: DataTable) {
        table.asMaps().forEach { row ->
            val title = row["titre"]!!.trim()
            val status = row["statut"]?.let(French::hikeStatus) ?: HikeStatus.OPEN
            val date = row["date"]?.let { LocalDate.parse(it.trim()) }
            val hike =
                data.hike(
                    title = title,
                    difficulty = row["difficulté"]?.let(French::difficulty) ?: TrailDifficulty.EASY,
                    status = status,
                    capacity = row["capacité"]?.trim()?.toInt() ?: 10,
                    schedule = data.defaultSchedule(status, date),
                    trailName = row["sentier"]?.trim() ?: "Sentier $title",
                )
            data.confirmedParticipants(hike.id, row["confirmés"]?.trim()?.toInt() ?: 0)
        }
    }

    @Given("""^(?:qu')?aucune randonnée n'est ouverte$""")
    fun noOpenHike() {
        hikes.findAll().filter { it.status == HikeStatus.OPEN }.forEach { data.withStatus(it, HikeStatus.CLOSED) }
    }

    @When("""^un visiteur liste les randonnées ouvertes$""")
    fun visitorListsOpenHikes() {
        world.attempt { world.browseResult = browseHikes.listOpenHikes() }
    }

    @When("""^je recherche les randonnées ouvertes avec le nom de sentier "([^"]*)"(?: et la difficulté "([^"]*)")?$""")
    fun searchByTrailName(
        trailName: String,
        difficulty: String?,
    ) = search(SearchOpenHikesQuery(trailName = trailName, difficulty = difficulty?.let(French::difficulty)))

    @When("""^je recherche les randonnées ouvertes à la date "([^"]*)"$""")
    fun searchByDate(date: String) = search(SearchOpenHikesQuery(date = LocalDate.parse(date)))

    @When("""^je recherche les randonnées ouvertes de difficulté "([^"]*)"$""")
    fun searchByDifficulty(difficulty: String) = search(SearchOpenHikesQuery(difficulty = French.difficulty(difficulty)))

    private fun search(query: SearchOpenHikesQuery) {
        world.attempt { world.browseResult = browseHikes.searchOpenHikes(query) }
    }

    @When("""^(il|un visiteur) liste les randonnées correspondant à son niveau d'habileté$""")
    fun listForSkillLevel(who: String) {
        val hikerId = if (who == "il") world.userId(world.currentHikerEmail!!) else data.visitorId()
        world.attempt { world.browseResult = browseHikes.listHikesForSkillLevel(ListHikesForSkillLevelQuery(hikerId)) }
    }

    @Then("""^le résultat contient exactement (.+?)( dans cet ordre)?$""")
    fun resultContainsExactly(
        titles: String,
        inOrder: String?,
    ) {
        assertNull(world.error, "Commande refusée : ${world.error?.message}")
        val expected = French.quotedValues(titles)
        val actual = world.browseResult.map { it.title }
        if (inOrder != null) {
            assertEquals(expected, actual)
        } else {
            assertEquals(expected.sorted(), actual.sorted())
        }
    }

    @Then("""^le résultat est vide$""")
    fun resultIsEmpty() {
        assertNull(world.error, "Commande refusée : ${world.error?.message}")
        assertTrue(world.browseResult.isEmpty(), "Résultat : ${world.browseResult.map { it.title }}")
    }

    @Then("""^chaque randonnée affiche ses informations, son organisateur et le nombre de places restantes$""")
    fun eachHikeShowsDetails() {
        assertTrue(world.browseResult.isNotEmpty())
        world.browseResult.forEach {
            assertTrue(it.title.isNotBlank() && it.trailName.isNotBlank() && it.description.isNotBlank())
            assertTrue(it.organizerName.isNotBlank(), "Organisateur de ${it.title}")
            assertTrue(it.remainingSpots >= 0)
            assertEquals(HikeStatus.OPEN, it.status)
        }
    }

    @Then("""^la randonnée "([^"]*)" affiche (\d+) places restantes$""")
    fun hikeShowsRemainingSpots(
        title: String,
        remainingSpots: Int,
    ) {
        assertEquals(remainingSpots, world.browseResult.single { it.title == title }.remainingSpots)
    }
}
