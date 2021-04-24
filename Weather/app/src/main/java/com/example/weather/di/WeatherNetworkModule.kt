package com.example.weather.di

import com.example.weather.data.RetrofitService
import com.example.weather.data.WeatherService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
object WeatherNetworkModule {
    @Provides
    fun provideWeatherService(): WeatherService = RetrofitService.retrofit.create(WeatherService::class.java)
}