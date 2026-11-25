package ca.cairn.hms.domain.signup

import ca.cairn.hms.domain.hike.Hike
import ca.cairn.hms.domain.hike.HikeStatus
import ca.cairn.hms.domain.hiker.Hiker
import ca.cairn.hms.domain.shared.DomainEventEmitter
import ca.cairn.hms.domain.shared.EntityNotFoundException
import ca.cairn.hms.domain.shared.violation
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

/** Façade du sous-domaine Inscriptions (inscriptions et liste d'attente). */
@Service
class SignUpFacade(
    private val signUpRepository: SignUpRepository,
    private val signUpFactory: SignUpFactory,
    private val eventEmitter: DomainEventEmitter,
) {
    fun getSignUp(signUpId: UUID): SignUp =
        signUpRepository.findById(signUpId) ?: throw EntityNotFoundException("inscription introuvable")

    fun confirmedSignUps(hikeId: UUID): List<SignUp> =
        signUpRepository.findByHikeId(hikeId).filter { it.status == SignUpStatus.CONFIRMED }

    /** Inscriptions en liste d'attente, dans l'ordre d'inscription. */
    fun waitlist(hikeId: UUID): List<SignUp> =
        signUpRepository.findByHikeId(hikeId)
            .filter { it.status == SignUpStatus.WAITLISTED }
            .sortedBy { it.signedUpAt }

    fun remainingSpots(hike: Hike): Int = maxOf(0, hike.capacity - confirmedSignUps(hike.id).size)

    /** Position (à partir de 1) dans la liste d'attente ; null si l'inscription n'y est pas. */
    fun waitlistPosition(signUp: SignUp): Int? =
        waitlist(signUp.hikeId).indexOfFirst { it.id == signUp.id }.takeIf { it >= 0 }?.plus(1)

    fun signUp(
        hike: Hike,
        hiker: Hiker,
        now: LocalDateTime,
    ): SignUp {
        if (!hiker.hasActiveMembership()) violation("adhésion inactive")
        if (!hike.acceptsSignUps()) violation("randonnée non ouverte")
        if (!hiker.skillLevel.canJoin(hike.difficulty)) violation("niveau d'habileté insuffisant")
        if (signUpRepository.findByHikeId(hike.id).any { it.hikerId == hiker.id && it.isActive }) violation("déjà inscrit")

        val status =
            if (hike.status == HikeStatus.OPEN && remainingSpots(hike) > 0) SignUpStatus.CONFIRMED else SignUpStatus.WAITLISTED
        val signUp = signUpFactory.createSignUp(hike.id, hiker.id, status, now)
        signUpRepository.save(signUp)
        if (status == SignUpStatus.CONFIRMED) {
            eventEmitter.emit(HikerSignedUp(signUp.id, hike.id, hiker.id, now))
        } else {
            eventEmitter.emit(HikerWaitlisted(signUp.id, hike.id, hiker.id, waitlistPosition(signUp)!!, now))
        }
        return signUp
    }

    /**
     * Annule une inscription. Si une place confirmée est libérée, le premier de la liste d'attente
     * est promu ; l'inscription promue est retournée.
     */
    fun cancel(
        signUp: SignUp,
        hike: Hike,
        now: LocalDateTime,
    ): SignUp? {
        if (hike.status == HikeStatus.CLOSED || hike.schedule.isDeadlinePassed(now.toLocalDate())) {
            violation("date limite d'inscription passée")
        }
        val wasConfirmed = signUp.status == SignUpStatus.CONFIRMED
        signUp.cancel()
        signUpRepository.save(signUp)
        eventEmitter.emit(SignUpCancelled(signUp.id, signUp.hikeId, signUp.hikerId, now))

        if (!wasConfirmed) return null
        val next = waitlist(hike.id).firstOrNull() ?: return null
        next.confirm()
        signUpRepository.save(next)
        eventEmitter.emit(WaitlistedHikerPromoted(next.id, next.hikeId, next.hikerId, now))
        return next
    }

    /** Annule toutes les inscriptions confirmées et en liste d'attente d'une randonnée annulée. */
    fun cancelAllForHike(
        hikeId: UUID,
        now: LocalDateTime,
    ): List<SignUp> {
        val active = signUpRepository.findByHikeId(hikeId).filter { it.isActive }
        active.forEach {
            it.cancel()
            signUpRepository.save(it)
            eventEmitter.emit(SignUpCancelled(it.id, it.hikeId, it.hikerId, now))
        }
        return active
    }
}
