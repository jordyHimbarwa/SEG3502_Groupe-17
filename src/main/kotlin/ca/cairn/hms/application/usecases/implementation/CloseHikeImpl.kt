package ca.cairn.hms.application.usecases.implementation

import ca.cairn.hms.application.dtos.queries.CloseHikeQuery
import ca.cairn.hms.application.usecases.CloseHike
import ca.cairn.hms.domain.hike.HikeFacade
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.LocalDateTime

@Service
class CloseHikeImpl(
    private val hikeFacade: HikeFacade,
    private val clock: Clock,
) : CloseHike {
    override fun closeHike(query: CloseHikeQuery) {
        hikeFacade.close(query.hikeId, LocalDateTime.now(clock))
    }
}
