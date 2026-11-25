package ca.cairn.hms.application.usecases.implementation

import ca.cairn.hms.application.dtos.queries.SendHikeReminderQuery
import ca.cairn.hms.application.services.EmailService
import ca.cairn.hms.application.services.WeatherService
import ca.cairn.hms.application.services.WeatherServiceUnavailableException
import ca.cairn.hms.application.usecases.SendHikeReminder
import ca.cairn.hms.domain.hike.HikeFacade
import ca.cairn.hms.domain.hike.HikeStatus
import ca.cairn.hms.domain.hiker.HikerFacade
import ca.cairn.hms.domain.shared.BusinessRuleViolationException
import ca.cairn.hms.domain.signup.SignUpFacade
import ca.cairn.hms.domain.trail.TrailFacade
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.LocalDate

@Service
class SendHikeReminderImpl(
    private val hikeFacade: HikeFacade,
    private val signUpFacade: SignUpFacade,
    private val hikerFacade: HikerFacade,
    private val trailFacade: TrailFacade,
    private val weatherService: WeatherService,
    private val emailService: EmailService,
    private val clock: Clock,
) : SendHikeReminder {
    override fun sendHikeReminder(query: SendHikeReminderQuery) {
        val hike = hikeFacade.getHike(query.hikeId)
        if (hike.status == HikeStatus.CANCELLED) return
        if (hike.schedule.startDate != LocalDate.now(clock).plusDays(1)) {
            throw BusinessRuleViolationException("le rappel est envoyé la veille de la randonnée")
        }
        val trail = trailFacade.findLocalTrail(hike.trailId)
        val weatherWarning =
            try {
                weatherService.forecast(trail?.startPoint ?: hike.trailId, hike.schedule.startDate).hasPrecipitation
            } catch (_: WeatherServiceUnavailableException) {
                false
            }

        signUpFacade.confirmedSignUps(hike.id).forEach {
            emailService.send(EmailMessages.reminder(hikerFacade.getHiker(it.hikerId), hike, trail, weatherWarning))
        }
    }
}
