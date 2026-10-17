package ca.cairn.hms.application.usecases

import ca.cairn.hms.application.dtos.queries.RegisterHikerQuery
import java.util.UUID

/** Cas d'utilisation : Enregistrer un randonneur (priorité 6). */
interface RegisterHiker {
    /** Crée le randonneur avec une adhésion active et retourne son identifiant. */
    fun registerHiker(query: RegisterHikerQuery): UUID
}
