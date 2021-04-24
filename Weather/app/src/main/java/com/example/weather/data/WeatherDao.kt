package com.example.weather.data

import androidx.room.*

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather_table")
    suspend fun getAll(): List<WeatherEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg weathers: WeatherEntity)

    @Update
    suspend fun update(weather: WeatherEntity)

    @Delete
    suspend fun delete(vararg weather: WeatherEntity)
}