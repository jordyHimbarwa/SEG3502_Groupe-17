package ca.cairn.hms.domain.trail

/** Copie locale des sentiers de la CCN. */
interface TrailRepository {
    fun findById(id: String): Trail?

    fun save(trail: Trail)
}
