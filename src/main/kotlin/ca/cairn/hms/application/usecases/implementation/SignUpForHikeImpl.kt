package ca.cairn.hms.application.usecases.implementation

import ca.cairn.hms.application.dtos.queries.SignUpForHikeQuery
import ca.cairn.hms.application.dtos.results.SignUpResultDto
import ca.cairn.hms.application.services.AuthenticationService
import ca.cairn.hms.application.services.EmailService
import ca.cairn.hms.application.usecases.SignUpForHike
import ca.cairn.hms.domain.hike.HikeFacade
import ca.cairn.hms.domain.hiker.HikerFacade
import ca.cairn.hms.domain.shared.Actor
import ca.cairn.hms.domain.signup.SignUpFacade
import ca.cairn.hms.domain.signup.SignUpStatus
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.LocalDateTime

@Service
class SignUpForHikeImpl(
    private val authenticationService: AuthenticationService,
    private val hikerFacade: HikerFacade,
    private val hikeFacade: HikeFacade,
    private val signUpFacade: SignUpFacade,
    private val emailService: EmailService,
    private val clock: Clock,
) : SignUpForHike {
    override fun signUpForHike(query: SignUpForHikeQuery): SignUpResultDto {
        authenticationService.requireAuthenticated(query.hikerId)
        val now = LocalDateTime.now(clock)
        val hiker = hikerFacade.getHiker(query.hikerId)
        val hike = hikeFacade.getHike(query.hikeId)

        val signUp = signUpFacade.signUp(hike, hiker, now)

        if (signUp.status == SignUpStatus.CONFIRMED) {
            if (signUpFacade.remainingSpots(hike) == 0) hikeFacade.markFull(hike.id, Actor.User(hiker.id), now)
            emailService.send(EmailMessages.signUpConfirmed(hiker, hike))
            return SignUpResultDto(signUp.id, signUp.status, null)
        }
        val position = signUpFacade.waitlistPosition(signUp)!!
        emailService.send(EmailMessages.waitlisted(hiker, hike, position))
        return SignUpResultDto(signUp.id, signUp.status, position)
    }
}
