package ca.cairn.hms.application.services

data class EmailMessage(
    val to: String,
    val subject: String,
    val body: String,
)

/** Service de courriel externe. */
interface EmailService {
    fun send(message: EmailMessage)
}
