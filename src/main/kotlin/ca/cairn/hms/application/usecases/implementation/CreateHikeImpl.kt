package ca.cairn.hms.application.usecases.implementation

import ca.cairn.hms.application.dtos.queries.CreateHikeQuery
import ca.cairn.hms.application.services.AuthenticationService
import ca.cairn.hms.application.services.CcnServiceUnavailableException
import ca.cairn.hms.application.services.CcnTrailService
import ca.cairn.hms.application.usecases.CreateHike
import ca.cairn.hms.domain.hike.HikeFacade
import ca.cairn.hms.domain.hiker.HikerFacade
import ca.cairn.hms.domain.shared.BusinessRuleViolationException
import ca.cairn.hms.domain.shared.UnauthorizedException
import ca.cairn.hms.domain.trail.Trail
import ca.cairn.hms.domain.trail.TrailFacade
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.LocalDateTime
import java.util.UUID

@Service
class CreateHikeImpl(
    private val authenticationService: AuthenticationService,
    private val ccnTrailService: CcnTrailService,
    private val hikerFacade: HikerFacade,
    private val trailFacade: TrailFacade,
    private val hikeFacade: HikeFacade,
    private val clock: Clock,
) : CreateHike {
    override fun createHike(query: CreateHikeQuery): UUID {
        authenticationService.requireAuthenticated(query.organizerId)
        val organizer = hikerFacade.findHiker(query.organizerId)
        val certification = organizer?.leaderCertification ?: throw UnauthorizedException()
        val trail = findTrail(query.trailId) ?: throw BusinessRuleViolationException("sentier inconnu de la CCN")

        val hike =
            hikeFacade.createHike(
                organizerId = organizer.id,
                certification = certification,
                trail = trail,
                title = query.title,
                description = query.description,
                startDateTime = query.startDateTime,
                expectedDuration = query.expectedDuration,
                signUpDeadline = query.signUpDeadline,
                capacity = query.capacity,
                now = LocalDateTime.now(clock),
            )
        return hike.id
    }

    /** Consulte la CCN et rafraîchit la copie locale ; utilise la copie locale si la CCN est indisponible. */
    private fun findTrail(trailId: String): Trail? =
        try {
            ccnTrailService.findTrail(trailId)?.also(trailFacade::refreshLocalCopy)
        } catch (_: CcnServiceUnavailableException) {
            trailFacade.findLocalTrail(trailId)
        }
}
