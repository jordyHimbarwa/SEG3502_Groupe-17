package ca.cairn.hms.contracts.steps

import ca.cairn.hms.application.dtos.queries.SendHikeReminderQuery
import ca.cairn.hms.application.services.WeatherCondition
import ca.cairn.hms.application.services.WeatherForecast
import ca.cairn.hms.application.usecases.SendHikeReminder
import ca.cairn.hms.application.usecases.implementation.EmailMessages
import ca.cairn.hms.contracts.support.ScenarioContext
import ca.cairn.hms.contracts.support.TestData
import ca.cairn.hms.domain.hike.HikeStatus
import ca.cairn.hms.stubs.EmailServiceStub
import ca.cairn.hms.stubs.HikeRepositoryStub
import ca.cairn.hms.stubs.TrailRepositoryStub
import ca.cairn.hms.stubs.WeatherServiceStub
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import java.time.format.DateTimeFormatter

class SendHikeReminderSteps(
    private val world: ScenarioContext,
    private val data: TestData,
    private val hikes: HikeRepositoryStub,
    private val trails: TrailRepositoryStub,
    private val weather: WeatherServiceStub,
    private val emails: EmailServiceStub,
    private val sendHikeReminder: SendHikeReminder,
) {
    private val reminders get() = emails.sent.filter { it.subject == EmailMessages.REMINDER_SUBJECT }

    @Given("""^une randonnée "([^"]*)" sur le sentier "([^"]*)" qui a lieu demain à (\d+) h$""")
    fun hikeTomorrow(
        title: String,
        trailName: String,
        hour: Int,
    ) {
        data.hike(title, status = HikeStatus.CLOSED, schedule = data.tomorrowAt(hour), trailName = trailName)
    }

    @Given("""^(?:que )?le service météorologique prévoit (?:du soleil|"([^"]*)") au point de départ demain$""")
    fun forecast(precipitation: String?) {
        weather.forecast =
            WeatherForecast(
                when (precipitation) {
                    null -> WeatherCondition.SUNNY
                    "pluie" -> WeatherCondition.RAIN
                    "neige" -> WeatherCondition.SNOW
                    else -> error("Prévision inconnue : $precipitation")
                },
            )
    }

    @Given("""^(?:que )?le service météorologique est indisponible$""")
    fun weatherUnavailable() {
        weather.available = false
    }

    @Given("""^(?:que )?la randonnée "([^"]*)" est annulée$""")
    fun hikeIsCancelled(title: String) {
        data.withStatus(hikes.findById(world.hikeId(title))!!, HikeStatus.CANCELLED)
    }

    @When("""^le système envoie le rappel de la randonnée "([^"]*)"$""")
    fun systemSendsReminder(title: String) {
        world.attempt { sendHikeReminder.sendHikeReminder(SendHikeReminderQuery(world.hikeId(title))) }
    }

    @Then("""^un courriel de rappel est envoyé (?:uniquement )?à "([^"]*)" et "([^"]*)"$""")
    fun reminderSentTo(
        first: String,
        second: String,
    ) {
        assertNull(world.error, "Rappel refusé : ${world.error?.message}")
        assertEquals(setOf(first, second), reminders.map { it.to }.toSet())
        assertEquals(2, reminders.size)
    }

    @Then("""^chaque courriel indique la date, l'heure de début, le sentier et le point de départ$""")
    fun eachReminderHasDetails() {
        val hike = hikes.findById(world.currentHikeId!!)!!
        val trail = trails.findById(hike.trailId)!!
        assertTrue(reminders.isNotEmpty())
        reminders.forEach {
            assertTrue(it.body.contains(hike.schedule.startDate.format(DateTimeFormatter.ISO_LOCAL_DATE)), "Date")
            assertTrue(it.body.contains(hike.schedule.startDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))), "Heure de début")
            assertTrue(it.body.contains(trail.name), "Sentier")
            assertTrue(it.body.contains(trail.startPoint), "Point de départ")
        }
    }

    @Then("""^aucun courriel ne contient d'avertissement météorologique$""")
    fun noWeatherWarning() {
        assertTrue(reminders.isNotEmpty())
        assertTrue(reminders.none { it.body.contains(EmailMessages.WEATHER_WARNING) })
    }

    @Then("""^chaque courriel de rappel contient un avertissement météorologique$""")
    fun everyReminderHasWeatherWarning() {
        assertTrue(reminders.isNotEmpty())
        assertTrue(reminders.all { it.body.contains(EmailMessages.WEATHER_WARNING) })
    }

    @Then("""^aucun courriel de rappel n'est envoyé$""")
    fun noReminderSent() {
        assertTrue(reminders.isEmpty())
    }
}
