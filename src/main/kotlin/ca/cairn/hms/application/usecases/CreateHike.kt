package ca.cairn.hms.application.usecases

import ca.cairn.hms.application.dtos.queries.CreateHikeQuery
import java.util.UUID

/** Cas d'utilisation : Créer une randonnée (priorité 1). */
interface CreateHike {
    /** Enregistre la randonnée comme ouverte et retourne son identifiant unique. */
    fun createHike(query: CreateHikeQuery): UUID
}
