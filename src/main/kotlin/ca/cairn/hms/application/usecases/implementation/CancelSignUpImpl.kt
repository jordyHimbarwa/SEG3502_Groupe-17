package ca.cairn.hms.application.usecases.implementation

import ca.cairn.hms.application.dtos.queries.CancelSignUpQuery
import ca.cairn.hms.application.services.AuthenticationService
import ca.cairn.hms.application.services.EmailService
import ca.cairn.hms.application.usecases.CancelSignUp
import ca.cairn.hms.domain.hike.HikeFacade
import ca.cairn.hms.domain.hike.HikeStatus
import ca.cairn.hms.domain.hiker.HikerFacade
import ca.cairn.hms.domain.shared.Actor
import ca.cairn.hms.domain.shared.UnauthorizedException
import ca.cairn.hms.domain.signup.SignUpFacade
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.LocalDateTime

@Service
class CancelSignUpImpl(
    private val authenticationService: AuthenticationService,
    private val hikerFacade: HikerFacade,
    private val hikeFacade: HikeFacade,
    private val signUpFacade: SignUpFacade,
    private val emailService: EmailService,
    private val clock: Clock,
) : CancelSignUp {
    override fun cancelSignUp(query: CancelSignUpQuery) {
        authenticationService.requireAuthenticated(query.hikerId)
        val now = LocalDateTime.now(clock)
        val signUp = signUpFacade.getSignUp(query.signUpId)
        if (signUp.hikerId != query.hikerId) throw UnauthorizedException()
        val hike = hikeFacade.getHike(signUp.hikeId)

        val promoted = signUpFacade.cancel(signUp, hike, now)

        if (promoted != null) {
            emailService.send(EmailMessages.promoted(hikerFacade.getHiker(promoted.hikerId), hike))
        } else if (hike.status == HikeStatus.FULL && signUpFacade.remainingSpots(hike) > 0) {
            hikeFacade.reopen(hike.id, Actor.User(query.hikerId), now)
        }
    }
}
