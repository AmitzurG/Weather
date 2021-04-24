package com.example.weather.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import com.example.weather.data.WeatherEntity
import com.example.weather.data.WeatherRepository
import com.example.weather.data.WeatherSharedPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {

    @Inject lateinit var repository: WeatherRepository
    @Inject lateinit var sharedpreferences: WeatherSharedPreferences

    var units = "metric"
        get() = sharedpreferences.preferences.getString(WeatherSharedPreferences.UNITS_KEY, "metric") ?: "metric"
        set(value) {
            sharedpreferences.preferences.edit().putString(WeatherSharedPreferences.UNITS_KEY, value).apply()
            field = value
        }

    private val first
        get() = sharedpreferences.preferences.getBoolean(WeatherSharedPreferences.FIRST_KEY, true)

    var citiesWeather = mutableListOf<WeatherEntity>()

    fun getWeather(city: String, units: String = "metric") = liveData(Dispatchers.IO) {
        val weather = repository.getWeather(city, units)
        emit(weather)
    }

    fun getCitiesWeather(units: String = "metric") = liveData(Dispatchers.IO) {
        if (repository.getAllWeathers().isEmpty() && first) {
            sharedpreferences.preferences.edit().putBoolean(WeatherSharedPreferences.FIRST_KEY, false).apply()
            addCitiesToDb() // save the weather cities persistently
        }
        citiesWeather = getWeatherCities(units) as MutableList<WeatherEntity>
        emit(citiesWeather)
    }

    fun getWeatherByGeoCoordinates(lat: String, lon: String, units: String = "metric") = liveData(Dispatchers.IO) {
        val weather = repository.getWeatherByGeoCoordinates(lat, lon, units)
        emit(weather)
    }

    fun getHourlyWeather(lat: String, lon: String, units: String = "metric") = liveData(Dispatchers.IO) {
        val hourlyWeather = repository.getHourlyWeather(lat, lon, units)
        val hourlyWeatherList = hourlyWeather.hourly.filterIndexed { index, _ -> index % 3 == 0 } // get weather data for each 3rd hour
        emit(hourlyWeatherList)
    }

    fun deleteCityWeather(vararg weathers: WeatherEntity) = liveData(Dispatchers.IO) {
        for (weather in weathers) {
            citiesWeather.remove(weather)
        }
        repository.deleteWeather(*weathers)
        emit(citiesWeather)
    }

    fun restoreCitiesWeather(units: String = "metric") = liveData(Dispatchers.IO) {
        addCitiesToDb()
        citiesWeather = getWeatherCities(units)
        emit(citiesWeather)
    }

    private val cities = listOf("jerusalem", "tel-aviv", "ramla", "raanana", "eilat", "london", "york") //listOf("petah-tikva", "jerusalem", "tel-aviv", "ramla", "yavne")
    private suspend fun addCitiesToDb() {
        val citiesWeather = mutableListOf<WeatherEntity>()
        for (city in cities) {
            val weather = repository.getWeather(city, units)
            citiesWeather.add(
                WeatherEntity(weather.id, weather.name)
            )
        }
        repository.insertWeather(citiesWeather)
    }

    private suspend fun getWeatherCities(units: String = "metric"): MutableList<WeatherEntity> {
        val citiesWeather = mutableListOf<WeatherEntity>()
        val cities = getCities()
        for (city in cities) {
            val weather = repository.getWeather(city, units)
            citiesWeather.add(WeatherEntity(
                weather.id,
                weather.name,
                weather.weather[0].description,
                weather.main.temp,
                weather.main.temp_min,
                weather.main.temp_max,
                weather.weather[0].icon
            ))
        }
        return citiesWeather
    }

    private suspend fun getCities(): List<String> {
        val citiesWeather = repository.getAllWeathers()
        return citiesWeather.map { it.name }
    }
}