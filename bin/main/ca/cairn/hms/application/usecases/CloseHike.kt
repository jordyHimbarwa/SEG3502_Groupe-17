package ca.cairn.hms.application.usecases

import ca.cairn.hms.application.dtos.queries.CloseHikeQuery

/** Cas d'utilisation : Fermer une randonnée (priorité 2). Déclenché par le système. */
interface CloseHike {
    fun closeHike(query: CloseHikeQuery)
}
