package ca.cairn.hms.domain.hike

import ca.cairn.hms.domain.hiker.LeaderCertification
import ca.cairn.hms.domain.shared.Actor
import ca.cairn.hms.domain.shared.DomainEventEmitter
import ca.cairn.hms.domain.shared.EntityNotFoundException
import ca.cairn.hms.domain.shared.violation
import ca.cairn.hms.domain.trail.Trail
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

/** Façade du sous-domaine Randonnées. */
@Service
class HikeFacade(
    private val hikeRepository: HikeRepository,
    private val hikeFactory: HikeFactory,
    private val eventEmitter: DomainEventEmitter,
) {
    fun getHike(hikeId: UUID): Hike = hikeRepository.findById(hikeId) ?: throw EntityNotFoundException("randonnée introuvable")

    /** Randonnées à l'état « ouverte », triées par date et heure de début. */
    fun findOpenHikes(): List<Hike> =
        hikeRepository.findAll()
            .filter { it.status == HikeStatus.OPEN }
            .sortedBy { it.schedule.startDateTime }

    @Suppress("LongParameterList")
    fun createHike(
        organizerId: UUID,
        certification: LeaderCertification,
        trail: Trail,
        title: String,
        description: String,
        startDateTime: LocalDateTime,
        expectedDuration: Duration,
        signUpDeadline: LocalDate,
        capacity: Int,
        now: LocalDateTime,
    ): Hike {
        val date = startDateTime.toLocalDate()
        if (!trail.hikingAllowed) violation("randonnée non permise sur ce sentier")
        if (!certification.allowsLeading(trail.difficulty)) violation("niveau d'organisateur inférieur à la difficulté du sentier")
        if (!trail.isOpenOn(date)) violation("date hors de la saison d'ouverture du sentier")
        val schedule = HikeSchedule(startDateTime, expectedDuration, signUpDeadline)
        if (!certification.isValidOn(date)) violation("certification expirée à la date de la randonnée")
        if (capacity <= 0) violation("capacité invalide")
        if (title.isBlank()) violation("informations incomplètes")

        val hike = hikeFactory.createHike(organizerId, trail.id, title, description, schedule, trail.difficulty, capacity, now)
        hikeRepository.save(hike)
        eventEmitter.emit(HikeCreated(hike.id, organizerId, now))
        return hike
    }

    fun markFull(
        hikeId: UUID,
        by: Actor,
        now: LocalDateTime,
    ) {
        val hike = getHike(hikeId)
        hike.markFull(by, now)
        hikeRepository.save(hike)
        eventEmitter.emit(HikeBecameFull(hike.id, by, now))
    }

    fun reopen(
        hikeId: UUID,
        by: Actor,
        now: LocalDateTime,
    ) {
        val hike = getHike(hikeId)
        hike.reopen(by, now)
        hikeRepository.save(hike)
        eventEmitter.emit(HikeReopened(hike.id, by, now))
    }

    fun close(
        hikeId: UUID,
        now: LocalDateTime,
    ) {
        val hike = getHike(hikeId)
        if (hike.close(now)) {
            hikeRepository.save(hike)
            eventEmitter.emit(HikeClosed(hike.id, now))
        }
    }

    fun cancel(
        hikeId: UUID,
        by: Actor,
        reason: String,
        now: LocalDateTime,
    ) {
        val hike = getHike(hikeId)
        hike.cancel(by, reason, now)
        hikeRepository.save(hike)
        eventEmitter.emit(HikeCancelled(hike.id, reason, by, now))
    }
}
