package ca.cairn.hms.domain.signup

import ca.cairn.hms.domain.shared.DomainEvent
import java.time.LocalDateTime
import java.util.UUID

data class HikerSignedUp(
    val signUpId: UUID,
    val hikeId: UUID,
    val hikerId: UUID,
    override val occurredAt: LocalDateTime,
) : DomainEvent

data class HikerWaitlisted(
    val signUpId: UUID,
    val hikeId: UUID,
    val hikerId: UUID,
    val position: Int,
    override val occurredAt: LocalDateTime,
) : DomainEvent

data class SignUpCancelled(
    val signUpId: UUID,
    val hikeId: UUID,
    val hikerId: UUID,
    override val occurredAt: LocalDateTime,
) : DomainEvent

data class WaitlistedHikerPromoted(
    val signUpId: UUID,
    val hikeId: UUID,
    val hikerId: UUID,
    override val occurredAt: LocalDateTime,
) : DomainEvent
