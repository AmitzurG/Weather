package com.example.weather.data

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherSharedPreferences @Inject constructor(@ApplicationContext context : Context) {
    companion object {
        private const val SHARED_PREFERENCE_NAME = "weatherSharedPreference"
        const val UNITS_KEY = "unitsKey"
        const val FIRST_KEY = "first"
    }

    val preferences: SharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
}