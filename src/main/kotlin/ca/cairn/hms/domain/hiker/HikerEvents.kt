package ca.cairn.hms.domain.hiker

import ca.cairn.hms.domain.shared.DomainEvent
import ca.cairn.hms.domain.trail.TrailDifficulty
import java.time.LocalDateTime
import java.util.UUID

data class HikerRegistered(
    val hikerId: UUID,
    val email: String,
    override val occurredAt: LocalDateTime,
) : DomainEvent

data class HikerInformationUpdated(
    val hikerId: UUID,
    override val occurredAt: LocalDateTime,
) : DomainEvent

data class MembershipStatusChanged(
    val hikerId: UUID,
    val previousStatus: MembershipStatus,
    val newStatus: MembershipStatus,
    val changedBy: UUID,
    override val occurredAt: LocalDateTime,
) : DomainEvent

data class LeaderCertificationRecorded(
    val hikerId: UUID,
    val certificateNumber: String,
    val organizerLevel: TrailDifficulty,
    val recordedBy: UUID,
    override val occurredAt: LocalDateTime,
) : DomainEvent
