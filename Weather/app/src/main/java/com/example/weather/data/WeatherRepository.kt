package com.example.weather.data

import javax.inject.Inject

class WeatherRepository @Inject constructor(private val weatherService: WeatherService, private val weatherDao: WeatherDao) {

    // network
    suspend fun getWeather(city: String, units: String = "metric") = weatherService.getWeather(city, units)
    suspend fun getWeatherByGeoCoordinates(lat: String, lon: String, units: String = "metric") = weatherService.getWeatherByGeoCoordinates(lat, lon, units)
    suspend fun getHourlyWeather(lat: String, lon: String, units: String = "metric") = weatherService.getHourlyWeather(lat, lon, units)

    // database
    suspend fun getAllWeathers() = weatherDao.getAll()
    suspend fun insertWeather(weathers: List<WeatherEntity>) = weatherDao.insert(*weathers.toTypedArray())
    suspend fun updateWeather(weather: WeatherEntity) = weatherDao.update(weather)
    suspend fun deleteWeather(vararg weathers: WeatherEntity) = weatherDao.delete(*weathers)
}

