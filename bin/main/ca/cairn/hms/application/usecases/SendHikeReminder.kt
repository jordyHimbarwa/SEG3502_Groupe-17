package ca.cairn.hms.application.usecases

import ca.cairn.hms.application.dtos.queries.SendHikeReminderQuery

/** Cas d'utilisation : Envoyer un rappel de randonnée (priorité 9). Déclenché par le système. */
interface SendHikeReminder {
    fun sendHikeReminder(query: SendHikeReminderQuery)
}
