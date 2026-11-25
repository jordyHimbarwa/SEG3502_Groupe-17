package ca.cairn.hms.contracts.steps

import ca.cairn.hms.contracts.support.French
import ca.cairn.hms.contracts.support.ScenarioContext
import ca.cairn.hms.contracts.support.TestData
import ca.cairn.hms.domain.hiker.MembershipStatus
import ca.cairn.hms.domain.hiker.SkillLevel
import io.cucumber.java.en.Given

/** Mise en place des utilisateurs, commune à plusieurs contrats. */
class HikerSteps(
    private val world: ScenarioContext,
    private val data: TestData,
) {
    @Given("""^un randonneur connecté(?: "([^"]*)")?(?: de niveau "([^"]*)")?(?: avec une adhésion "([^"]*)")?( sans certification)?$""")
    fun loggedInHiker(
        email: String?,
        skillLevel: String?,
        membership: String?,
        @Suppress("UNUSED_PARAMETER") withoutCertification: String?,
    ) {
        val address = email ?: "randonneur@exemple.ca"
        data.hiker(
            address,
            skillLevel?.let(French::skillLevel) ?: SkillLevel.INTERMEDIATE,
            membership?.let(French::membershipStatus) ?: MembershipStatus.ACTIVE,
        )
        world.currentHikerEmail = address
    }

    @Given("""^un randonneur "([^"]*)"(?: avec une adhésion "([^"]*)")?$""")
    fun hiker(
        email: String,
        membership: String?,
    ) {
        data.hiker(email, membership = membership?.let(French::membershipStatus) ?: MembershipStatus.ACTIVE)
    }

    @Given("""^un randonneur existant identifié par "([^"]*)"$""")
    fun existingHiker(email: String) {
        data.hiker(email)
    }

    @Given("""^un administrateur connecté$""")
    fun loggedInAdministrator() {
        data.administrator("admin@exemple.ca")
    }
}
