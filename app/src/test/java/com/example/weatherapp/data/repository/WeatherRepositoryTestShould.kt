package com.example.weatherapp.data.repository

import com.example.weatherapp.data.models.CurrentWeather
import com.example.weatherapp.data.remote.WeatherService
import com.example.weatherapp.utils.BaseUnitTes
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito

class WeatherRepositoryTestShould : BaseUnitTes() {
    private val weatherService: WeatherService = mock()
    private val currentWeather: CurrentWeather = mock()
    private val exception = RuntimeException("Something went wrong")


    @ExperimentalCoroutinesApi
    @Test
    fun getCurrentWeatherFromService() = runBlockingTest {
        val repository = mockSuccessfulCase()
        repository.getCurrentWeather("Standard")
        Mockito.verify(weatherService, times(1)).fetchCurrentWeather("Standard")
    }

    @ExperimentalCoroutinesApi
    @Test
    fun emitCurrentWeatherFromService() = runBlockingTest {
        val repository = mockSuccessfulCase()
        assertEquals(currentWeather, repository.getCurrentWeather("Standard").first().getOrNull())
    }

    @ExperimentalCoroutinesApi
    @Test
    fun propagateErrors() = runBlockingTest {
        val repository = mockFailure()
        assertEquals(exception, repository.getCurrentWeather("Standard").first().exceptionOrNull())
    }

    private suspend fun mockFailure(): WeatherRepository {
        whenever(weatherService.fetchCurrentWeather("Standard")).thenReturn(
            flow {
                emit(Result.failure<CurrentWeather>(exception))
            }
        )
        return WeatherRepository(weatherService)
    }

    private suspend fun mockSuccessfulCase(): WeatherRepository {
        whenever(weatherService.fetchCurrentWeather("Standard")).thenReturn(
            flow {
                emit(Result.success(currentWeather))
            }
        )
        return WeatherRepository(weatherService)
    }

}