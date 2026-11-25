package ca.cairn.hms.domain.shared

import java.time.LocalDateTime

/** Événement de domaine émis pour chaque changement d'état d'un agrégat. */
interface DomainEvent {
    val occurredAt: LocalDateTime
}

/** Publie les événements de domaine. L'implémentation réelle viendra au livrable 3. */
interface DomainEventEmitter {
    fun emit(event: DomainEvent)
}
