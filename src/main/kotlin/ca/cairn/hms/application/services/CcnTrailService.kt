package ca.cairn.hms.application.services

import ca.cairn.hms.domain.trail.Trail

class CcnServiceUnavailableException : RuntimeException("données de la CCN indisponibles")

/** Données des sentiers publiées par la CCN, traduites dans le modèle du domaine (couche anticorruption). */
interface CcnTrailService {
    /**
     * @return le sentier, ou null s'il est inconnu de la CCN.
     * @throws CcnServiceUnavailableException si la CCN ne répond pas.
     */
    fun findTrail(trailId: String): Trail?
}
