package com.example.weatherapp.data.repository

import com.example.weatherapp.data.models.CurrentWeather
import com.example.weatherapp.data.remote.WeatherService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class WeatherRepository @Inject constructor(private val weatherService: WeatherService) {
    suspend fun getCurrentWeather(units: String): Flow<Result<CurrentWeather>> =
        weatherService.fetchCurrentWeather(units).map {
            if (it.isSuccess)
                Result.success(it.getOrNull()!!)
            else
                Result.failure(it.exceptionOrNull()!!)
        }

}