package ca.cairn.hms.stubs

import ca.cairn.hms.application.services.AuthenticationService
import ca.cairn.hms.application.services.CcnServiceUnavailableException
import ca.cairn.hms.application.services.CcnTrailService
import ca.cairn.hms.application.services.EmailMessage
import ca.cairn.hms.application.services.EmailService
import ca.cairn.hms.application.services.InvitationCodeService
import ca.cairn.hms.application.services.WeatherForecast
import ca.cairn.hms.application.services.WeatherService
import ca.cairn.hms.application.services.WeatherServiceUnavailableException
import ca.cairn.hms.domain.shared.DomainEvent
import ca.cairn.hms.domain.shared.DomainEventEmitter
import ca.cairn.hms.domain.trail.Trail
import io.cucumber.spring.ScenarioScope
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

/** Enregistre les événements émis pour que les contrats puissent les vérifier. */
@Component
@ScenarioScope
class DomainEventEmitterStub : DomainEventEmitter {
    val events = mutableListOf<DomainEvent>()

    override fun emit(event: DomainEvent) {
        events += event
    }
}

/** Enregistre les courriels au lieu de les envoyer. */
@Component
@ScenarioScope
class EmailServiceStub : EmailService {
    val sent = mutableListOf<EmailMessage>()

    override fun send(message: EmailMessage) {
        sent += message
    }

    fun sentTo(email: String): List<EmailMessage> = sent.filter { it.to.equals(email, ignoreCase = true) }
}

@Component
@ScenarioScope
class WeatherServiceStub : WeatherService {
    var forecast: WeatherForecast? = null
    var available = true

    override fun forecast(
        location: String,
        date: LocalDate,
    ): WeatherForecast {
        if (!available) throw WeatherServiceUnavailableException()
        return forecast ?: throw WeatherServiceUnavailableException()
    }
}

@Component
@ScenarioScope
class CcnTrailServiceStub : CcnTrailService {
    val trails = linkedMapOf<String, Trail>()
    var available = true
    var calls = 0

    override fun findTrail(trailId: String): Trail? {
        calls++
        if (!available) throw CcnServiceUnavailableException()
        return trails[trailId]
    }
}

@Component
@ScenarioScope
class AuthenticationServiceStub : AuthenticationService {
    private val authenticated = mutableSetOf<UUID>()
    private val administrators = mutableSetOf<UUID>()
    val credentials = mutableMapOf<UUID, Pair<String, String>>()

    fun logIn(userId: UUID) {
        authenticated += userId
    }

    fun grantAdministrator(userId: UUID) {
        administrators += userId
    }

    override fun isAuthenticated(userId: UUID): Boolean = userId in authenticated

    override fun isAdministrator(userId: UUID): Boolean = userId in administrators

    override fun createCredentials(
        userId: UUID,
        email: String,
        password: String,
    ) {
        credentials[userId] = email to password
    }
}

@Component
@ScenarioScope
class InvitationCodeServiceStub : InvitationCodeService {
    val validCodes = mutableSetOf<String>()

    override fun isValid(code: String): Boolean = code in validCodes

    override fun markUsed(code: String) {
        validCodes -= code
    }
}

/** Horloge contrôlée par les contrats ; par défaut le 1er octobre 2026 à 8 h. */
@Component
@ScenarioScope
class ClockStub : Clock() {
    private var current: Instant = LocalDateTime.of(2026, 10, 1, 8, 0).atZone(ZONE).toInstant()

    override fun getZone(): ZoneId = ZONE

    override fun withZone(zone: ZoneId): Clock = fixed(current, zone)

    override fun instant(): Instant = current

    fun now(): LocalDateTime = LocalDateTime.ofInstant(current, ZONE)

    fun today(): LocalDate = now().toLocalDate()

    fun setTo(dateTime: LocalDateTime) {
        current = dateTime.atZone(ZONE).toInstant()
    }

    companion object {
        val ZONE: ZoneId = ZoneId.of("America/Toronto")
    }
}
