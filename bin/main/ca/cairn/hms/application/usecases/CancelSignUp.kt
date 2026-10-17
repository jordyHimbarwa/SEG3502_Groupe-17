package ca.cairn.hms.application.usecases

import ca.cairn.hms.application.dtos.queries.CancelSignUpQuery

/** Cas d'utilisation : Annuler une inscription (priorité 4). */
interface CancelSignUp {
    fun cancelSignUp(query: CancelSignUpQuery)
}
