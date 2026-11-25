package ca.cairn.hms.domain.signup

import ca.cairn.hms.domain.shared.violation
import java.time.LocalDateTime
import java.util.UUID

/** Entité racine de l'agrégat Inscription. La liste d'attente est ordonnée selon [signedUpAt]. */
class SignUp(
    val id: UUID,
    val hikeId: UUID,
    val hikerId: UUID,
    val signedUpAt: LocalDateTime,
    status: SignUpStatus,
) {
    var status: SignUpStatus = status
        private set

    val isActive: Boolean get() = status != SignUpStatus.CANCELLED

    fun cancel() {
        if (status == SignUpStatus.CANCELLED) violation("inscription déjà annulée")
        status = SignUpStatus.CANCELLED
    }

    /** Promotion depuis la liste d'attente. */
    fun confirm() {
        if (status != SignUpStatus.WAITLISTED) violation("inscription non en liste d'attente")
        status = SignUpStatus.CONFIRMED
    }
}
