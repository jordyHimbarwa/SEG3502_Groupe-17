package ca.cairn.hms.domain.hiker

import java.util.UUID

interface HikerRepository {
    fun findById(id: UUID): Hiker?

    fun findByEmail(email: String): Hiker?

    fun save(hiker: Hiker)
}
