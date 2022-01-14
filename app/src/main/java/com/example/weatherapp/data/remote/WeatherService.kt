package com.example.weatherapp.data.remote

import com.example.weatherapp.data.models.CurrentWeather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class WeatherService @Inject constructor(private val weatherApi: WeatherApi) {
    suspend fun fetchCurrentWeather(units: String): Flow<Result<CurrentWeather>> {
        return flow {
            emit(
                Result.success(
                    weatherApi.getCurrentWeather(
                        41.311081,
                        69.240562,
                        "a6b99ad23cc5a4823e4429589d831d4e",
                        units
                    )
                )
            )
        }.catch {
            emit(Result.failure(RuntimeException("Something went wrong")))
        }
    }
}