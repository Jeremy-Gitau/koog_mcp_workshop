package org.mcp_workshop.source.remote.models

import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    val location: Location,
    val current: Current,
    val forecast: Forecast
)

@Serializable
data class Location(
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val tz_id: String,
    val localtime_epoch: Long,
    val localtime: String
)

@Serializable
data class Current(
    val last_updated_epoch: Long,
    val last_updated: String,
)

@Serializable
data class Forecast(
    val forecastday: List<ForecastDay>
)

@Serializable
data class ForecastDay(
    val date: String,
    val date_epoch: Long,
    val day: Day
)

@Serializable
data class Day(
    val maxtemp_c: Double,
    val maxtemp_f: Double,
    val mintemp_c: Double,
    val mintemp_f: Double,
    val avgtemp_c: Double,
    val avgtemp_f: Double,
    val maxwind_mph: Double,
    val maxwind_kph: Double,
    val totalprecip_mm: Double,
    val totalsnow_cm: Double,
    val avgvis_km: Double,
    val avghumidity: Double,
    val daily_will_it_rain: Int,
    val daily_chance_of_rain: Int,
    val daily_will_it_snow: Int,
    val daily_chance_of_snow: Int,
    val condition: Condition,
    val uv: Double
)

@Serializable
data class Condition(
    val text: String,
    val icon: String,
    val code: Int
)