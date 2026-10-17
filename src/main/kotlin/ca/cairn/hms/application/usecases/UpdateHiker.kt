package ca.cairn.hms.application.usecases

import ca.cairn.hms.application.dtos.queries.RecordLeaderCertificationQuery
import ca.cairn.hms.application.dtos.queries.UpdateHikerInformationQuery
import ca.cairn.hms.application.dtos.queries.UpdateMembershipStatusQuery

/** Cas d'utilisation : Mettre à jour un randonneur (priorité 7). */
interface UpdateHiker {
    /** Le randonneur met à jour ses propres informations. */
    fun updateHikerInformation(query: UpdateHikerInformationQuery)

    /** Un administrateur change le statut d'adhésion d'un randonneur. */
    fun updateMembershipStatus(query: UpdateMembershipStatusQuery)

    /** Un administrateur enregistre une certification de chef de randonnée et un niveau d'organisateur. */
    fun recordLeaderCertification(query: RecordLeaderCertificationQuery)
}
