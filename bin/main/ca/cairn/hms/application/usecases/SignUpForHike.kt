package ca.cairn.hms.application.usecases

import ca.cairn.hms.application.dtos.queries.SignUpForHikeQuery
import ca.cairn.hms.application.dtos.results.SignUpResultDto

/** Cas d'utilisation : S'inscrire à une randonnée (priorité 3). */
interface SignUpForHike {
    fun signUpForHike(query: SignUpForHikeQuery): SignUpResultDto
}
