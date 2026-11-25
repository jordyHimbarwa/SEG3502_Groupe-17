package ca.cairn.hms.contracts.support

import ca.cairn.hms.application.dtos.results.HikeSummaryDto
import ca.cairn.hms.application.dtos.results.SignUpResultDto
import ca.cairn.hms.domain.shared.DomainException
import io.cucumber.spring.ScenarioScope
import org.springframework.stereotype.Component
import java.util.UUID

/** État partagé entre les définitions d'étapes d'un même scénario. */
@Component
@ScenarioScope
class ScenarioContext {
    /** Identifiant de chaque utilisateur (randonneur ou administrateur) désigné par son courriel. */
    val userIds = mutableMapOf<String, UUID>()
    val hikeIds = mutableMapOf<String, UUID>()

    /** Dernière randonnée mentionnée ou créée. */
    var currentHikeId: UUID? = null

    /** Randonneur connecté qui joue le rôle principal du scénario (« il », « son »). */
    var currentHikerEmail: String? = null

    /** Randonneur concerné par la dernière action (« son inscription », « sa certification »). */
    var subjectEmail: String? = null
    var administratorId: UUID? = null

    var lastSignUpId: UUID? = null
    var signUpResult: SignUpResultDto? = null
    var createdHikeId: UUID? = null
    var registeredHikerId: UUID? = null
    var browseResult: List<HikeSummaryDto> = emptyList()
    var snapshot: Any? = null
    var countBefore: Int = 0

    var error: DomainException? = null

    /** Exécute une commande et conserve le refus éventuel pour les étapes « Alors ». */
    fun attempt(command: () -> Unit) {
        error = null
        try {
            command()
        } catch (e: DomainException) {
            error = e
        }
    }

    fun userId(email: String): UUID = userIds[email] ?: error("Utilisateur inconnu dans le scénario : $email")

    fun hikeId(title: String): UUID = hikeIds[title] ?: error("Randonnée inconnue dans le scénario : $title")
}
