package ca.cairn.hms.application.dtos.results

import ca.cairn.hms.domain.signup.SignUpStatus
import java.util.UUID

data class SignUpResultDto(
    val signUpId: UUID,
    val status: SignUpStatus,
    /** Position dans la liste d'attente (à partir de 1) ; null si l'inscription est confirmée. */
    val waitlistPosition: Int?,
)
