package com.example.weatherapp.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.models.CurrentWeather
import com.example.weatherapp.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@InternalCoroutinesApi
@HiltViewModel
class HomeViewModel @Inject constructor(private val weatherRepository: WeatherRepository) :
    ViewModel() {
    val weatherDetails: MutableLiveData<Result<CurrentWeather>> = MutableLiveData()
    val loader = MutableLiveData<Boolean>()

    init {
        getWeatherDetails("Standard")
    }

    @InternalCoroutinesApi
    fun getWeatherDetails(unit: String) {
        viewModelScope.launch {
            loader.postValue(true)
            weatherRepository.getCurrentWeather(unit)
                .onEach {
                    loader.postValue(false)
                }
                .collect {
                    weatherDetails.postValue(it)
                }
        }
    }

}