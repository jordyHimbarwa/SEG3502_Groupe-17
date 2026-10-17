package ca.cairn.hms.application.dtos.queries

import ca.cairn.hms.domain.hiker.MembershipStatus
import java.util.UUID

data class UpdateMembershipStatusQuery(
    val administratorId: UUID,
    val hikerId: UUID,
    val membershipStatus: MembershipStatus,
)
