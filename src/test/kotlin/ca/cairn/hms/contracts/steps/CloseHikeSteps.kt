package ca.cairn.hms.contracts.steps

import ca.cairn.hms.application.dtos.queries.CancelSignUpQuery
import ca.cairn.hms.application.dtos.queries.CloseHikeQuery
import ca.cairn.hms.application.dtos.queries.SignUpForHikeQuery
import ca.cairn.hms.application.usecases.CancelSignUp
import ca.cairn.hms.application.usecases.CloseHike
import ca.cairn.hms.application.usecases.SignUpForHike
import ca.cairn.hms.contracts.support.French
import ca.cairn.hms.contracts.support.ScenarioContext
import ca.cairn.hms.contracts.support.TestData
import ca.cairn.hms.domain.hike.HikeStatus
import ca.cairn.hms.domain.hiker.SkillLevel
import ca.cairn.hms.domain.shared.BusinessRuleViolationException
import ca.cairn.hms.domain.signup.SignUpStatus
import ca.cairn.hms.stubs.SignUpRepositoryStub
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf

class CloseHikeSteps(
    private val world: ScenarioContext,
    private val data: TestData,
    private val signUps: SignUpRepositoryStub,
    private val closeHike: CloseHike,
    private val signUpForHike: SignUpForHike,
    private val cancelSignUp: CancelSignUp,
) {
    @Given("""^une randonnée "([^"]*)" "?(ouverte|complète|fermée|annulée)"? dont la date limite d'inscription (est|n'est pas) atteinte$""")
    fun hikeWithDeadline(
        title: String,
        status: String,
        reached: String,
    ) {
        val hikeStatus = French.hikeStatus(status)
        val capacity = if (hikeStatus == HikeStatus.FULL) 5 else 10
        val hike = data.hike(title, status = hikeStatus, capacity = capacity, schedule = data.deadlineReachedSchedule(reached == "est"))
        if (hikeStatus == HikeStatus.FULL) data.confirmedParticipants(hike.id, capacity)
    }

    @When("""^le système ferme la randonnée "([^"]*)"$""")
    fun systemClosesHike(title: String) {
        world.currentHikeId = world.hikeId(title)
        world.attempt { closeHike.closeHike(CloseHikeQuery(world.hikeId(title))) }
    }

    @Then("""^toute nouvelle inscription à "([^"]*)" est refusée$""")
    fun anyNewSignUpIsRefused(title: String) {
        val hiker = data.hiker("nouveau@exemple.ca", SkillLevel.EXPERT)
        world.attempt { signUpForHike.signUpForHike(SignUpForHikeQuery(hiker.id, world.hikeId(title))) }
        assertInstanceOf(BusinessRuleViolationException::class.java, world.error)
    }

    @Then("""^toute annulation d'inscription à "([^"]*)" est refusée$""")
    fun anyCancellationIsRefused(title: String) {
        val signUp = data.signUp(world.hikeId(title), "inscrit@exemple.ca", SignUpStatus.CONFIRMED)
        world.attempt { cancelSignUp.cancelSignUp(CancelSignUpQuery(signUp.hikerId, signUp.id)) }
        assertInstanceOf(BusinessRuleViolationException::class.java, world.error)
        assertEquals(SignUpStatus.CONFIRMED, signUps.findById(signUp.id)!!.status)
    }
}
