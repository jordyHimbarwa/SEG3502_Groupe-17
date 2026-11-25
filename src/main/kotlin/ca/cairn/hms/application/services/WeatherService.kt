package ca.cairn.hms.application.services

import java.time.LocalDate

enum class WeatherCondition { SUNNY, CLOUDY, RAIN, SNOW }

data class WeatherForecast(val condition: WeatherCondition) {
    val hasPrecipitation: Boolean get() = condition == WeatherCondition.RAIN || condition == WeatherCondition.SNOW
}

class WeatherServiceUnavailableException : RuntimeException("service météorologique indisponible")

/** Service météorologique externe. */
interface WeatherService {
    /** @throws WeatherServiceUnavailableException si les prévisions ne peuvent pas être obtenues. */
    fun forecast(
        location: String,
        date: LocalDate,
    ): WeatherForecast
}
