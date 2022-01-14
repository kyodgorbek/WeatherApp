package com.example.weatherapp.data.remote

import com.example.weatherapp.data.models.CurrentWeather
import retrofit2.http.GET
import retrofit2.http.Query


interface WeatherApi {
    @GET("/data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String,
        @Query("units") units: String
    ): CurrentWeather
}