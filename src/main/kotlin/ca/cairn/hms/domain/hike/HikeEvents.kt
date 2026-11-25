package ca.cairn.hms.domain.hike

import ca.cairn.hms.domain.shared.Actor
import ca.cairn.hms.domain.shared.DomainEvent
import java.time.LocalDateTime
import java.util.UUID

data class HikeCreated(
    val hikeId: UUID,
    val organizerId: UUID,
    override val occurredAt: LocalDateTime,
) : DomainEvent

data class HikeBecameFull(
    val hikeId: UUID,
    val changedBy: Actor,
    override val occurredAt: LocalDateTime,
) : DomainEvent

data class HikeReopened(
    val hikeId: UUID,
    val changedBy: Actor,
    override val occurredAt: LocalDateTime,
) : DomainEvent

data class HikeClosed(
    val hikeId: UUID,
    override val occurredAt: LocalDateTime,
) : DomainEvent

data class HikeCancelled(
    val hikeId: UUID,
    val reason: String,
    val changedBy: Actor,
    override val occurredAt: LocalDateTime,
) : DomainEvent
