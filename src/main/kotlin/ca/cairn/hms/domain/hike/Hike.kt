package ca.cairn.hms.domain.hike

import ca.cairn.hms.domain.shared.Actor
import ca.cairn.hms.domain.shared.violation
import ca.cairn.hms.domain.trail.TrailDifficulty
import java.time.LocalDateTime
import java.util.UUID

/** Entité racine de l'agrégat Randonnée. */
class Hike(
    val id: UUID,
    val organizerId: UUID,
    val trailId: String,
    val title: String,
    val description: String,
    val schedule: HikeSchedule,
    val difficulty: TrailDifficulty,
    val capacity: Int,
    status: HikeStatus,
    statusHistory: List<StatusChange>,
    cancellationReason: String? = null,
) {
    init {
        if (capacity <= 0) violation("capacité invalide")
    }

    var status: HikeStatus = status
        private set
    var cancellationReason: String? = cancellationReason
        private set
    private val history: MutableList<StatusChange> = statusHistory.toMutableList()

    /** Journal des transitions, chacune consignée avec son auteur. */
    val statusHistory: List<StatusChange> get() = history.toList()

    /** Une randonnée accepte des inscriptions (confirmées ou en liste d'attente) tant qu'elle est ouverte ou complète. */
    fun acceptsSignUps(): Boolean = status == HikeStatus.OPEN || status == HikeStatus.FULL

    fun markFull(
        by: Actor,
        at: LocalDateTime,
    ) {
        if (status != HikeStatus.OPEN) violation("randonnée non ouverte")
        transitionTo(HikeStatus.FULL, by, at)
    }

    fun reopen(
        by: Actor,
        at: LocalDateTime,
    ) {
        if (status != HikeStatus.FULL) violation("randonnée non complète")
        transitionTo(HikeStatus.OPEN, by, at)
    }

    /** Ferme la randonnée à sa date limite. Retourne false si elle n'était ni ouverte ni complète (rien à faire). */
    fun close(at: LocalDateTime): Boolean {
        if (!acceptsSignUps()) return false
        if (!schedule.isDeadlineReached(at.toLocalDate())) violation("date limite non atteinte")
        transitionTo(HikeStatus.CLOSED, Actor.System, at)
        return true
    }

    fun cancel(
        by: Actor,
        reason: String,
        at: LocalDateTime,
    ) {
        if (reason.isBlank()) violation("motif requis")
        if (status == HikeStatus.CANCELLED) violation("randonnée déjà annulée")
        if (status == HikeStatus.COMPLETED || schedule.hasStarted(at)) violation("randonnée déjà commencée")
        cancellationReason = reason
        transitionTo(HikeStatus.CANCELLED, by, at)
    }

    private fun transitionTo(
        newStatus: HikeStatus,
        by: Actor,
        at: LocalDateTime,
    ) {
        status = newStatus
        history += StatusChange(newStatus, by, at)
    }

    companion object {
        /** Nouvelle randonnée à l'état « ouverte », étape consignée avec son créateur. */
        @Suppress("LongParameterList")
        fun open(
            id: UUID,
            organizerId: UUID,
            trailId: String,
            title: String,
            description: String,
            schedule: HikeSchedule,
            difficulty: TrailDifficulty,
            capacity: Int,
            createdAt: LocalDateTime,
        ) = Hike(
            id = id,
            organizerId = organizerId,
            trailId = trailId,
            title = title,
            description = description,
            schedule = schedule,
            difficulty = difficulty,
            capacity = capacity,
            status = HikeStatus.OPEN,
            statusHistory = listOf(StatusChange(HikeStatus.OPEN, Actor.User(organizerId), createdAt)),
        )
    }
}
