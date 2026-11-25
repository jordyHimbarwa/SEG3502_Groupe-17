package ca.cairn.hms.domain.trail

import org.springframework.stereotype.Service

/** Façade du sous-domaine Sentiers. */
@Service
class TrailFacade(private val trailRepository: TrailRepository) {
    fun findLocalTrail(trailId: String): Trail? = trailRepository.findById(trailId)

    /** Met à jour la copie locale avec la version la plus récente publiée par la CCN. */
    fun refreshLocalCopy(trail: Trail) = trailRepository.save(trail)
}
