package com.example.weatherapp.ui.home

import com.example.weatherapp.data.models.CurrentWeather
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.utils.BaseUnitTes
import com.example.weatherapp.utils.captureValues
import com.example.weatherapp.utils.getValueForTest
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.mockito.Mockito.verify


@InternalCoroutinesApi
class HomeViewModelTestShould : BaseUnitTes() {

    private val weatherRepository: WeatherRepository = mock()
    private val currentWeather: CurrentWeather = mock()
    private val expected = Result.success(currentWeather)
    private val exception = RuntimeException("Something went wrong")

    @ExperimentalCoroutinesApi
    @Test
    fun getCurrentWeatherFromRepository() = runBlockingTest {
        val viewModel = mockSuccessfulCase()
        viewModel.weatherDetails.getValueForTest()
        verify(weatherRepository, times(1)).getCurrentWeather("Standard")
    }


    @ExperimentalCoroutinesApi
    @Test
    fun showSpinnerWhileLoading() = runBlockingTest {
        val viewModel = mockSuccessfulCase()
        viewModel.loader.captureValues {
            viewModel.weatherDetails.getValueForTest()
            assertEquals(false, values[0])
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun emitsCurrentWeatherFromRepository() = runBlockingTest {
        val viewModel = mockSuccessfulCase()
        assertEquals(expected, viewModel.weatherDetails.getValueForTest())
    }


    @Test
    fun emitErrorWhenReceiveError() {
        val viewModel = mockErrorCase()
        assertEquals(exception, viewModel.weatherDetails.getValueForTest()!!.exceptionOrNull())
    }


    @ExperimentalCoroutinesApi
    @Test
    fun closeLoaderAfterCurrentWeatherLoad() = runBlockingTest {
        val viewModel = mockErrorCase()
        viewModel.loader.captureValues {
            viewModel.weatherDetails.getValueForTest()
            assertEquals(false, values.last())
        }
    }


    @InternalCoroutinesApi
    private fun mockErrorCase(): HomeViewModel {
        runBlocking {
            whenever(weatherRepository.getCurrentWeather("Standard")).thenReturn(
                flow {
                    emit(Result.failure<CurrentWeather>(exception))
                }
            )
        }
        return HomeViewModel(weatherRepository)
    }


    @InternalCoroutinesApi
    private fun mockSuccessfulCase(): HomeViewModel {
        runBlocking {
            whenever(weatherRepository.getCurrentWeather("Standard")).thenReturn(
                flow {
                    emit(expected)
                }
            )
        }
        return HomeViewModel(weatherRepository)
    }

}