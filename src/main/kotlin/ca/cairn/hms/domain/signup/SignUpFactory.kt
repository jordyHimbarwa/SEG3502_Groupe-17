package ca.cairn.hms.domain.signup

import java.time.LocalDateTime
import java.util.UUID

interface SignUpFactory {
    fun createSignUp(
        hikeId: UUID,
        hikerId: UUID,
        status: SignUpStatus,
        signedUpAt: LocalDateTime,
    ): SignUp
}
