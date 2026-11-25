package ca.cairn.hms.application.usecases.implementation

import ca.cairn.hms.application.dtos.queries.CancelHikeQuery
import ca.cairn.hms.application.services.AuthenticationService
import ca.cairn.hms.application.services.EmailService
import ca.cairn.hms.application.usecases.CancelHike
import ca.cairn.hms.domain.hike.HikeFacade
import ca.cairn.hms.domain.hiker.HikerFacade
import ca.cairn.hms.domain.shared.Actor
import ca.cairn.hms.domain.shared.UnauthorizedException
import ca.cairn.hms.domain.signup.SignUpFacade
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.LocalDateTime

@Service
class CancelHikeImpl(
    private val authenticationService: AuthenticationService,
    private val hikeFacade: HikeFacade,
    private val hikerFacade: HikerFacade,
    private val signUpFacade: SignUpFacade,
    private val emailService: EmailService,
    private val clock: Clock,
) : CancelHike {
    override fun cancelHike(query: CancelHikeQuery) {
        authenticationService.requireAuthenticated(query.requesterId)
        val hike = hikeFacade.getHike(query.hikeId)
        if (hike.organizerId != query.requesterId && !authenticationService.isAdministrator(query.requesterId)) {
            throw UnauthorizedException()
        }
        val now = LocalDateTime.now(clock)

        hikeFacade.cancel(hike.id, Actor.User(query.requesterId), query.reason, now)
        val cancelledSignUps = signUpFacade.cancelAllForHike(hike.id, now)

        cancelledSignUps.forEach {
            emailService.send(EmailMessages.hikeCancelled(hikerFacade.getHiker(it.hikerId), hike, query.reason))
        }
    }
}
