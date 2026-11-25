package ca.cairn.hms.domain.signup

import java.util.UUID

interface SignUpRepository {
    fun findById(id: UUID): SignUp?

    fun findByHikeId(hikeId: UUID): List<SignUp>

    fun save(signUp: SignUp)
}
