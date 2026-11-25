package ca.cairn.hms.contracts.support

import ca.cairn.hms.domain.hike.Hike
import ca.cairn.hms.domain.hike.HikeSchedule
import ca.cairn.hms.domain.hike.HikeStatus
import ca.cairn.hms.domain.hike.StatusChange
import ca.cairn.hms.domain.hiker.EmergencyContact
import ca.cairn.hms.domain.hiker.Hiker
import ca.cairn.hms.domain.hiker.LeaderCertification
import ca.cairn.hms.domain.hiker.MembershipStatus
import ca.cairn.hms.domain.hiker.PersonalInformation
import ca.cairn.hms.domain.hiker.SkillLevel
import ca.cairn.hms.domain.hiker.Waiver
import ca.cairn.hms.domain.shared.Actor
import ca.cairn.hms.domain.signup.SignUp
import ca.cairn.hms.domain.signup.SignUpStatus
import ca.cairn.hms.domain.trail.OpeningSeason
import ca.cairn.hms.domain.trail.Trail
import ca.cairn.hms.domain.trail.TrailDifficulty
import ca.cairn.hms.stubs.AuthenticationServiceStub
import ca.cairn.hms.stubs.ClockStub
import ca.cairn.hms.stubs.HikeRepositoryStub
import ca.cairn.hms.stubs.HikerRepositoryStub
import ca.cairn.hms.stubs.SignUpRepositoryStub
import ca.cairn.hms.stubs.TrailRepositoryStub
import io.cucumber.spring.ScenarioScope
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

/**
 * Met en place l'état initial des scénarios (« Étant donné ») directement dans les stubs,
 * sans passer par les cas d'utilisation testés.
 */
@Component
@ScenarioScope
class TestData(
    private val world: ScenarioContext,
    private val clock: ClockStub,
    private val hikers: HikerRepositoryStub,
    private val hikes: HikeRepositoryStub,
    private val signUps: SignUpRepositoryStub,
    private val trails: TrailRepositoryStub,
    private val authentication: AuthenticationServiceStub,
) {
    private var participantCount = 0
    private var signUpCount = 0

    // ---------------------------------------------------------------- utilisateurs

    /** Crée (ou remplace) un randonneur connecté. */
    fun hiker(
        email: String,
        skillLevel: SkillLevel = SkillLevel.INTERMEDIATE,
        membership: MembershipStatus = MembershipStatus.ACTIVE,
        certification: LeaderCertification? = null,
    ): Hiker {
        val id = world.userIds[email] ?: UUID.randomUUID()
        val name = email.substringBefore('@').replaceFirstChar { it.uppercase() }
        val hiker =
            Hiker(
                id = id,
                email = email,
                personalInformation = PersonalInformation(name, "Test", "613-555-0100", "1 rue Principale, Ottawa"),
                dateOfBirth = LocalDate.of(1990, 1, 1),
                emergencyContact = EmergencyContact("Contact de $name", "613-555-0101"),
                skillLevel = skillLevel,
                waiver = Waiver(clock.today()),
                membershipStatus = membership,
                leaderCertification = certification,
            )
        hikers.save(hiker)
        authentication.logIn(id)
        world.userIds[email] = id
        return hiker
    }

    /** Randonneur existant, ou nouveau randonneur par défaut. */
    fun existingOrNewHiker(email: String): Hiker = world.userIds[email]?.let { hikers.findById(it) } ?: hiker(email)

    fun organizer(
        email: String,
        organizerLevel: TrailDifficulty = TrailDifficulty.VERY_DIFFICULT,
        certificationExpiry: LocalDate = LocalDate.of(2030, 12, 31),
    ): Hiker =
        hiker(
            email,
            SkillLevel.EXPERT,
            certification = LeaderCertification("CHR-${email.hashCode().toUInt()}", LocalDate.of(2026, 1, 1), certificationExpiry, organizerLevel),
        )

    fun administrator(email: String): UUID {
        val id = world.userIds.getOrPut(email) { UUID.randomUUID() }
        authentication.logIn(id)
        authentication.grantAdministrator(id)
        world.administratorId = id
        return id
    }

    /** Identifiant d'un visiteur sans compte ni session. */
    fun visitorId(): UUID = UUID.randomUUID()

    // ---------------------------------------------------------------- sentiers

    fun trail(
        id: String,
        name: String,
        difficulty: TrailDifficulty = TrailDifficulty.EASY,
        season: OpeningSeason = OpeningSeason.ALL_YEAR,
        hikingAllowed: Boolean = true,
    ) = Trail(id, name, difficulty, season, hikingAllowed, "Stationnement du $name")

    // ---------------------------------------------------------------- randonnées

    /** Calendrier cohérent avec le statut demandé, relatif à l'horloge du scénario. */
    fun defaultSchedule(
        status: HikeStatus,
        date: LocalDate? = null,
    ): HikeSchedule {
        val today = clock.today()
        return when {
            date != null -> HikeSchedule(date.atTime(9, 0), Duration.ofHours(3), date.minusDays(7))
            status == HikeStatus.CLOSED -> HikeSchedule(today.plusDays(3).atTime(9, 0), Duration.ofHours(3), today.minusDays(1))
            else -> HikeSchedule(today.plusDays(10).atTime(9, 0), Duration.ofHours(3), today.plusDays(3))
        }
    }

    fun deadlineReachedSchedule(reached: Boolean): HikeSchedule {
        val today = clock.today()
        return if (reached) {
            HikeSchedule(today.plusDays(5).atTime(9, 0), Duration.ofHours(3), today)
        } else {
            HikeSchedule(today.plusDays(7).atTime(9, 0), Duration.ofHours(3), today.plusDays(3))
        }
    }

    fun tomorrowAt(hour: Int): HikeSchedule {
        val tomorrow = clock.today().plusDays(1)
        return HikeSchedule(tomorrow.atTime(LocalTime.of(hour, 0)), Duration.ofHours(3), clock.today().minusDays(1))
    }

    @Suppress("LongParameterList")
    fun hike(
        title: String,
        difficulty: TrailDifficulty = TrailDifficulty.EASY,
        status: HikeStatus = HikeStatus.OPEN,
        capacity: Int = 10,
        schedule: HikeSchedule = defaultSchedule(status),
        organizerEmail: String = "organisateur@exemple.ca",
        trailName: String = "Sentier $title",
    ): Hike {
        val organizer = existingOrNewOrganizer(organizerEmail)
        val trail = trail("T-${title.hashCode().toUInt()}", trailName, difficulty)
        trails.save(trail)

        val created = clock.now().minusDays(20)
        val history = mutableListOf(StatusChange(HikeStatus.OPEN, Actor.User(organizer.id), created))
        if (status != HikeStatus.OPEN) history += StatusChange(status, Actor.System, created.plusDays(1))

        val hike =
            Hike(
                id = UUID.randomUUID(),
                organizerId = organizer.id,
                trailId = trail.id,
                title = title,
                description = "Randonnée $title",
                schedule = schedule,
                difficulty = difficulty,
                capacity = capacity,
                status = status,
                statusHistory = history,
                cancellationReason = if (status == HikeStatus.CANCELLED) "Annulée" else null,
            )
        hikes.save(hike)
        world.hikeIds[title] = hike.id
        world.currentHikeId = hike.id
        return hike
    }

    /** Remplace une randonnée par une copie ayant un autre statut. */
    fun withStatus(
        hike: Hike,
        status: HikeStatus,
    ): Hike {
        val copy =
            Hike(
                hike.id, hike.organizerId, hike.trailId, hike.title, hike.description, hike.schedule, hike.difficulty,
                hike.capacity, status, hike.statusHistory + StatusChange(status, Actor.System, clock.now()),
            )
        hikes.save(copy)
        return copy
    }

    private fun existingOrNewOrganizer(email: String): Hiker =
        world.userIds[email]?.let { hikers.findById(it) }?.takeIf { it.isOrganizer } ?: organizer(email)

    // ---------------------------------------------------------------- inscriptions

    fun signUp(
        hikeId: UUID,
        hikerEmail: String,
        status: SignUpStatus,
    ): SignUp {
        val hiker = existingOrNewHiker(hikerEmail)
        // Les inscriptions préexistantes sont antérieures à l'heure courante et ordonnées selon leur création.
        val at = clock.now().minusDays(1).plusMinutes((++signUpCount).toLong())
        val signUp = SignUp(UUID.randomUUID(), hikeId, hiker.id, at, status)
        signUps.save(signUp)
        return signUp
    }

    /** Ajoute des inscriptions confirmées de participants anonymes. */
    fun confirmedParticipants(
        hikeId: UUID,
        count: Int,
    ) = repeat(count) { signUp(hikeId, "participant${++participantCount}@exemple.ca", SignUpStatus.CONFIRMED) }

    fun waitlistedParticipants(
        hikeId: UUID,
        count: Int,
    ) = repeat(count) { signUp(hikeId, "attente${++participantCount}@exemple.ca", SignUpStatus.WAITLISTED) }

    fun signUpOf(
        email: String,
        hikeId: UUID,
    ): SignUp? = signUps.findByHikeId(hikeId).lastOrNull { it.hikerId == world.userIds[email] }

    fun moveClockTo(dateTime: LocalDateTime) = clock.setTo(dateTime)
}
