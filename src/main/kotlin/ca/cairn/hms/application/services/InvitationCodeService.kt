package ca.cairn.hms.application.services

/** Codes d'invitation émis par le club (hors du système). */
interface InvitationCodeService {
    fun isValid(code: String): Boolean

    fun markUsed(code: String)
}
