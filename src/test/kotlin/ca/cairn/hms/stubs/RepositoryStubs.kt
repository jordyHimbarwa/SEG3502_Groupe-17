package ca.cairn.hms.stubs

import ca.cairn.hms.domain.hike.Hike
import ca.cairn.hms.domain.hike.HikeRepository
import ca.cairn.hms.domain.hiker.Hiker
import ca.cairn.hms.domain.hiker.HikerRepository
import ca.cairn.hms.domain.signup.SignUp
import ca.cairn.hms.domain.signup.SignUpRepository
import ca.cairn.hms.domain.trail.Trail
import ca.cairn.hms.domain.trail.TrailRepository
import io.cucumber.spring.ScenarioScope
import org.springframework.stereotype.Component
import java.util.UUID

/** Dépôts en mémoire, recréés pour chaque scénario. */
@Component
@ScenarioScope
class HikeRepositoryStub : HikeRepository {
    private val hikes = linkedMapOf<UUID, Hike>()

    override fun findById(id: UUID): Hike? = hikes[id]

    override fun findAll(): List<Hike> = hikes.values.toList()

    override fun save(hike: Hike) {
        hikes[hike.id] = hike
    }

    fun count(): Int = hikes.size
}

@Component
@ScenarioScope
class HikerRepositoryStub : HikerRepository {
    private val hikers = linkedMapOf<UUID, Hiker>()

    override fun findById(id: UUID): Hiker? = hikers[id]

    override fun findByEmail(email: String): Hiker? = hikers.values.find { it.email.equals(email.trim(), ignoreCase = true) }

    override fun save(hiker: Hiker) {
        hikers[hiker.id] = hiker
    }

    fun count(): Int = hikers.size
}

@Component
@ScenarioScope
class SignUpRepositoryStub : SignUpRepository {
    private val signUps = linkedMapOf<UUID, SignUp>()

    override fun findById(id: UUID): SignUp? = signUps[id]

    override fun findByHikeId(hikeId: UUID): List<SignUp> = signUps.values.filter { it.hikeId == hikeId }

    override fun save(signUp: SignUp) {
        signUps[signUp.id] = signUp
    }

    fun findAll(): List<SignUp> = signUps.values.toList()
}

@Component
@ScenarioScope
class TrailRepositoryStub : TrailRepository {
    private val trails = linkedMapOf<String, Trail>()

    override fun findById(id: String): Trail? = trails[id]

    override fun save(trail: Trail) {
        trails[trail.id] = trail
    }
}
