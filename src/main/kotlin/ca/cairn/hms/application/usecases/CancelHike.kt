package ca.cairn.hms.application.usecases

import ca.cairn.hms.application.dtos.queries.CancelHikeQuery

/** Cas d'utilisation : Annuler une randonnée (priorité 8). */
interface CancelHike {
    fun cancelHike(query: CancelHikeQuery)
}
