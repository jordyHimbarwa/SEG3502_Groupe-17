package ca.cairn.hms.application.usecases.implementation

import ca.cairn.hms.application.dtos.queries.ListHikesForSkillLevelQuery
import ca.cairn.hms.application.dtos.queries.SearchOpenHikesQuery
import ca.cairn.hms.application.dtos.results.HikeSummaryDto
import ca.cairn.hms.application.services.AuthenticationService
import ca.cairn.hms.application.usecases.BrowseHikes
import ca.cairn.hms.domain.hike.Hike
import ca.cairn.hms.domain.hike.HikeFacade
import ca.cairn.hms.domain.hiker.HikerFacade
import ca.cairn.hms.domain.signup.SignUpFacade
import ca.cairn.hms.domain.trail.TrailFacade
import org.springframework.stereotype.Service

@Service
class BrowseHikesImpl(
    private val authenticationService: AuthenticationService,
    private val hikeFacade: HikeFacade,
    private val hikerFacade: HikerFacade,
    private val signUpFacade: SignUpFacade,
    private val trailFacade: TrailFacade,
) : BrowseHikes {
    override fun listOpenHikes(): List<HikeSummaryDto> = hikeFacade.findOpenHikes().map(::toSummary)

    override fun searchOpenHikes(query: SearchOpenHikesQuery): List<HikeSummaryDto> =
        hikeFacade.findOpenHikes()
            .map(::toSummary)
            .filter { query.trailName == null || it.trailName.contains(query.trailName, ignoreCase = true) }
            .filter { query.date == null || it.startDateTime.toLocalDate() == query.date }
            .filter { query.difficulty == null || it.difficulty == query.difficulty }

    override fun listHikesForSkillLevel(query: ListHikesForSkillLevelQuery): List<HikeSummaryDto> {
        authenticationService.requireAuthenticated(query.hikerId)
        val hiker = hikerFacade.getHiker(query.hikerId)
        return hikeFacade.findOpenHikes()
            .filter { hiker.skillLevel.canJoin(it.difficulty) }
            .map(::toSummary)
    }

    private fun toSummary(hike: Hike) =
        HikeSummaryDto(
            hikeId = hike.id,
            title = hike.title,
            description = hike.description,
            trailName = trailFacade.findLocalTrail(hike.trailId)?.name ?: hike.trailId,
            difficulty = hike.difficulty,
            startDateTime = hike.schedule.startDateTime,
            expectedDuration = hike.schedule.expectedDuration,
            signUpDeadline = hike.schedule.signUpDeadline,
            organizerName = hikerFacade.findHiker(hike.organizerId)?.fullName ?: "",
            remainingSpots = signUpFacade.remainingSpots(hike),
            status = hike.status,
        )
}
