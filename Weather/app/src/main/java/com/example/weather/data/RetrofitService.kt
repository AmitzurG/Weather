package com.example.weather.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object RetrofitService {
    private const val baseUrl = "https://api.openweathermap.org/"
    const val appId = "9cb98905c73f974da31dab5c9bfeaff8"
    val retrofit = Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build()
}

interface WeatherService {
    // units is standard, metric or imperial
    @GET("data/2.5/weather/?appid=${RetrofitService.appId}")
    suspend fun getWeather(@Query("q") city: String, @Query("units") units: String = "metric"): WeatherObject

    @GET("data/2.5/weather/?appid=${RetrofitService.appId}")
    suspend fun getWeatherByGeoCoordinates(@Query("lat") lat: String, @Query("lon") lon: String, @Query("units") units: String = "metric"): WeatherObject

    @GET("data/2.5/onecall?exclude=current,minutely,daily,alerts&appid=${RetrofitService.appId}")
    suspend fun getHourlyWeather(@Query("lat") lat: String, @Query("lon") lon: String, @Query("units") units: String = "metric"): HourlyWeatherObject
}