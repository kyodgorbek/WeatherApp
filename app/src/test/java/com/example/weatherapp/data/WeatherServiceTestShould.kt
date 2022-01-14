package com.example.weatherapp.data

import com.example.weatherapp.data.models.CurrentWeather
import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.data.remote.WeatherService
import com.example.weatherapp.utils.BaseUnitTes
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.mockito.Mockito.times
import org.mockito.Mockito.verify


class WeatherServiceTestShould : BaseUnitTes() {
    lateinit var weatherService: WeatherService
    private val weatherApi: WeatherApi = mock()
    private val currentWeather: CurrentWeather = mock()


    @ExperimentalCoroutinesApi
    @Test
    fun fetchCurrentWeatherFromApi() = runBlockingTest {
        weatherService = WeatherService(weatherApi)
        weatherService.fetchCurrentWeather("Standard").first()
        verify(weatherApi, times(1)).getCurrentWeather(
            41.311081,
            69.240562,
            "a6b99ad23cc5a4823e4429589d831d4e",
            "Standard"
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun convertValuesOfResultAndEmitsThem() = runBlockingTest {
        mockSuccessfulCase()
        assertEquals(
            Result.success(currentWeather),
            weatherService.fetchCurrentWeather("Standard").first()
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun emitErrorResultWhenNetworkFails() = runBlockingTest {
        mockFailerCase()
        assertEquals(
            "Something went wrong",
            weatherService.fetchCurrentWeather("Standard").first().exceptionOrNull()?.message
        )
    }

    private suspend fun mockFailerCase() {
        whenever(
            weatherApi.getCurrentWeather(
                41.311081,
                69.240562,
                "a6b99ad23cc5a4823e4429589d831d4e",
                "Standard"
            )
        ).thenThrow(RuntimeException("Damn Backend Developer"))
        weatherService = WeatherService(weatherApi)
    }

    private suspend fun mockSuccessfulCase() {
        whenever(
            weatherApi.getCurrentWeather(
                41.311081,
                69.240562,
                "a6b99ad23cc5a4823e4429589d831d4e",
                "Standard"
            )
        ).thenReturn(currentWeather)
        weatherService = WeatherService(weatherApi)
    }


}