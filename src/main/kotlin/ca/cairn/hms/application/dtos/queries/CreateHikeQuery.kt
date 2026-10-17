package ca.cairn.hms.application.dtos.queries

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class CreateHikeQuery(
    val organizerId: UUID,
    val title: String,
    val description: String,
    /** Identifiant du sentier dans les données publiées par la CCN. */
    val trailId: String,
    val startDateTime: LocalDateTime,
    val expectedDuration: Duration,
    val signUpDeadline: LocalDate,
    val capacity: Int,
)
