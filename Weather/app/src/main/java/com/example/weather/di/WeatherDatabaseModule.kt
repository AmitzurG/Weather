package com.example.weather.di

import android.content.Context
import androidx.room.Room
import com.example.weather.data.WeatherDao
import com.example.weather.data.WeatherDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object WeatherDatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): WeatherDatabase {
        return Room.databaseBuilder(
                appContext,
                WeatherDatabase::class.java,
                "weather.db"
        ).build()
    }

    @Provides
    fun provideLogDao(database: WeatherDatabase): WeatherDao {
        return database.weatherDao()
    }
}