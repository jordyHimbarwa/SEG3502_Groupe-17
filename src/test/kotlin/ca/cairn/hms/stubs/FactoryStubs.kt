package ca.cairn.hms.stubs

import ca.cairn.hms.domain.hike.Hike
import ca.cairn.hms.domain.hike.HikeFactory
import ca.cairn.hms.domain.hike.HikeSchedule
import ca.cairn.hms.domain.hiker.EmergencyContact
import ca.cairn.hms.domain.hiker.Hiker
import ca.cairn.hms.domain.hiker.HikerFactory
import ca.cairn.hms.domain.hiker.PersonalInformation
import ca.cairn.hms.domain.hiker.SkillLevel
import ca.cairn.hms.domain.hiker.Waiver
import ca.cairn.hms.domain.signup.SignUp
import ca.cairn.hms.domain.signup.SignUpFactory
import ca.cairn.hms.domain.signup.SignUpStatus
import ca.cairn.hms.domain.trail.TrailDifficulty
import io.cucumber.spring.ScenarioScope
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Component
@ScenarioScope
class HikeFactoryStub : HikeFactory {
    override fun createHike(
        organizerId: UUID,
        trailId: String,
        title: String,
        description: String,
        schedule: HikeSchedule,
        difficulty: TrailDifficulty,
        capacity: Int,
        createdAt: LocalDateTime,
    ): Hike = Hike.open(UUID.randomUUID(), organizerId, trailId, title, description, schedule, difficulty, capacity, createdAt)
}

@Component
@ScenarioScope
class HikerFactoryStub : HikerFactory {
    override fun createHiker(
        email: String,
        personalInformation: PersonalInformation,
        dateOfBirth: LocalDate,
        emergencyContact: EmergencyContact,
        skillLevel: SkillLevel,
        waiver: Waiver,
    ): Hiker = Hiker(UUID.randomUUID(), email, personalInformation, dateOfBirth, emergencyContact, skillLevel, waiver)
}

@Component
@ScenarioScope
class SignUpFactoryStub : SignUpFactory {
    override fun createSignUp(
        hikeId: UUID,
        hikerId: UUID,
        status: SignUpStatus,
        signedUpAt: LocalDateTime,
    ): SignUp = SignUp(UUID.randomUUID(), hikeId, hikerId, signedUpAt, status)
}
