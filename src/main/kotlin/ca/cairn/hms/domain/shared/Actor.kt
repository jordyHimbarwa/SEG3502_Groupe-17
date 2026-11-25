package ca.cairn.hms.domain.shared

import java.util.UUID

/** Auteur d'un changement d'état : un utilisateur identifié ou le système (ordonnanceur). */
sealed interface Actor {
    data class User(val id: UUID) : Actor

    data object System : Actor
}
