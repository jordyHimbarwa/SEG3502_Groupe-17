package ca.cairn.hms.domain.hike

import java.util.UUID

interface HikeRepository {
    fun findById(id: UUID): Hike?

    fun findAll(): List<Hike>

    fun save(hike: Hike)
}
