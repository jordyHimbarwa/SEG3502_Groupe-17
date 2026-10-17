package ca.cairn.hms.application.usecases

import ca.cairn.hms.application.dtos.queries.ListHikesForSkillLevelQuery
import ca.cairn.hms.application.dtos.queries.SearchOpenHikesQuery
import ca.cairn.hms.application.dtos.results.HikeSummaryDto

/** Cas d'utilisation : Parcourir les randonnées (priorité 5). Accessible aux visiteurs. */
interface BrowseHikes {
    /** Liste toutes les randonnées ouvertes, triées par date. */
    fun listOpenHikes(): List<HikeSummaryDto>

    /** Recherche les randonnées ouvertes par nom de sentier, date ou difficulté, triées par date. */
    fun searchOpenHikes(query: SearchOpenHikesQuery): List<HikeSummaryDto>

    /** Liste les randonnées ouvertes auxquelles le randonneur connecté peut s'inscrire selon son niveau. */
    fun listHikesForSkillLevel(query: ListHikesForSkillLevelQuery): List<HikeSummaryDto>
}
