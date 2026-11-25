package ca.cairn.hms.contracts.support

import ca.cairn.hms.domain.hike.HikeStatus
import ca.cairn.hms.domain.hiker.MembershipStatus
import ca.cairn.hms.domain.hiker.SkillLevel
import ca.cairn.hms.domain.signup.SignUpStatus
import ca.cairn.hms.domain.trail.OpeningSeason
import ca.cairn.hms.domain.trail.TrailDifficulty

/** Traduction des termes français des contrats vers le modèle du domaine. */
object French {
    fun hikeStatus(term: String): HikeStatus =
        when (term.trim()) {
            "ouverte" -> HikeStatus.OPEN
            "complète" -> HikeStatus.FULL
            "fermée" -> HikeStatus.CLOSED
            "annulée" -> HikeStatus.CANCELLED
            "terminée" -> HikeStatus.COMPLETED
            else -> error("Statut de randonnée inconnu : $term")
        }

    fun signUpStatus(term: String): SignUpStatus =
        when (term.trim()) {
            "confirmée", "confirmées" -> SignUpStatus.CONFIRMED
            "en liste d'attente" -> SignUpStatus.WAITLISTED
            "annulée", "annulées" -> SignUpStatus.CANCELLED
            else -> error("Statut d'inscription inconnu : $term")
        }

    fun difficulty(term: String): TrailDifficulty =
        when (term.trim()) {
            "facile" -> TrailDifficulty.EASY
            "modéré", "modérée" -> TrailDifficulty.MODERATE
            "difficile" -> TrailDifficulty.DIFFICULT
            "très difficile" -> TrailDifficulty.VERY_DIFFICULT
            else -> error("Difficulté inconnue : $term")
        }

    fun skillLevel(term: String): SkillLevel =
        when (term.trim()) {
            "débutant" -> SkillLevel.BEGINNER
            "intermédiaire" -> SkillLevel.INTERMEDIATE
            "avancé" -> SkillLevel.ADVANCED
            "expert" -> SkillLevel.EXPERT
            else -> error("Niveau d'habileté inconnu : $term")
        }

    fun membershipStatus(term: String): MembershipStatus =
        when (term.trim()) {
            "active" -> MembershipStatus.ACTIVE
            "suspendue" -> MembershipStatus.SUSPENDED
            "expirée" -> MembershipStatus.EXPIRED
            else -> error("Statut d'adhésion inconnu : $term")
        }

    fun season(term: String): OpeningSeason =
        when (term.trim()) {
            "toute l'année" -> OpeningSeason.ALL_YEAR
            "printemps" -> OpeningSeason.SPRING
            "été" -> OpeningSeason.SUMMER
            "automne" -> OpeningSeason.FALL
            "hiver" -> OpeningSeason.WINTER
            else -> error("Saison inconnue : $term")
        }

    fun yesNo(term: String): Boolean =
        when (term.trim()) {
            "oui" -> true
            "non" -> false
            else -> error("Valeur oui/non inconnue : $term")
        }

    /** Extrait les valeurs entre guillemets d'une liste telle que `"A", "B" et "C"`. */
    fun quotedValues(text: String): List<String> = Regex("\"([^\"]*)\"").findAll(text).map { it.groupValues[1] }.toList()
}
