package com.example.weather.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

data class WeatherObject(
        val coord: Coord,
        val weather: Array<Weather>,
        val base: String,
        val main: Main,
        val visibility: Int,
        val wind: Wind,
        val clouds: Clouds,
        val dt: Long,
        val sys: Sys,
        val timezone: Int,
        val id: Long,
        val name: String,
        val cod: Int)

@Entity(tableName = "weather_table")
data class WeatherEntity(
        @PrimaryKey val id: Long,
        val name: String,
        @Ignore val description: String, // @Ignore not saved in the database, not save in db values that are changed frequently
        @Ignore val temp: Double,
        @Ignore val temp_min: Double,
        @Ignore val temp_max: Double,
        @Ignore val icon: String) {
        constructor(id: Long, name: String) : this(id, name, "", 0.0, 0.0, 0.0, "")
}

data class Coord(val lon: Double, val lat: Double)

data class Weather(val id: Int, val main: String, val description: String, val icon: String)

data class Main(val temp: Double, val feels_like: Double, val temp_min: Double, val temp_max: Double, val pressure: Int, val humidity: Int)

data class Wind(val speed: Double, val deg: Int)

data class Clouds(val all: Int)

data class Sys(val type: Int, val id: Int, val country: String, val sunrise: Long, val sunset: Long)

data class HourlyWeatherObject(
        val lat: Double,
        val lon: Double,
        val timezone: String,
        val timezone_offset: Int,
        val hourly: Array<Hourly>)

data class Hourly(
        val dt: Int,
        val temp: Double,
        val feels_like: Double,
        val pressure: Int,
        val humidity: Int,
        val dew_point: Double,
        val uvi: Double,
        val clouds: Int,
        val visibility: Int,
        val wind_speed: Double,
        val wind_deg: Int,
        val wind_gust: Double,
        val weather: Array<Weather>,
        val pop: Double)
