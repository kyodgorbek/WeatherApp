package com.example.weatherapp.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import com.example.weatherapp.R
import com.example.weatherapp.common.SharedPreferenceHelper
import com.example.weatherapp.data.models.CurrentWeather
import com.example.weatherapp.databinding.HomeFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Inject

@InternalCoroutinesApi
@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var homeFragmentBinding: HomeFragmentBinding

    private lateinit var selectedChoice: String
    private val options = arrayOf("Celsius", "Fahrenheit")

    @Inject
    lateinit var sharedPreferenceHelper: SharedPreferenceHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false)
        homeFragmentBinding.lifecycleOwner = this
        observeWeatherDetail()
        displayLoader()
        swipeToRefresh()
        homeFragmentBinding.unitsTextView.setOnClickListener {
            showUnitsDialog()
        }
        return homeFragmentBinding.root
    }

    private fun swipeToRefresh() {
        homeFragmentBinding.swipeRefreshId.setOnRefreshListener {
            homeViewModel.getWeatherDetails("Standard")
            homeFragmentBinding.swipeRefreshId.isRefreshing = false
        }
    }

    @SuppressLint("ShowToast")
    private fun observeWeatherDetail() {
        homeViewModel.weatherDetails.observe(this as LifecycleOwner) { weatherDetails ->
            if (weatherDetails.getOrNull() != null) {
                setUiForWeatherInformation(weatherDetails)
            } else {
                Snackbar.make(
                    homeFragmentBinding.constraintLayout,
                    weatherDetails.exceptionOrNull().toString(),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUiForWeatherInformation(currentWeather: Result<CurrentWeather>) {
        homeFragmentBinding.weatherInText.text = currentWeather.getOrNull()?.name.toString()
        homeFragmentBinding.weatherTemperature.text =
            currentWeather.getOrNull()?.main?.temp.toString() + " " +
                    sharedPreferenceHelper.getSelectedTemperatureUnit().toString()
        currentWeather.getOrNull()?.weather?.map {
            homeFragmentBinding.weatherMain.text = it.main
        }
        homeFragmentBinding.humidityText.text =
            currentWeather.getOrNull()?.main?.humidity.toString() + " " + "%"
        homeFragmentBinding.pressureText.text =
            currentWeather.getOrNull()?.main?.pressure.toString() + " " + "hPa"
        homeFragmentBinding.windSpeedText.text =
            currentWeather.getOrNull()?.wind?.speed.toString() + " " + "m"
    }

    private fun displayLoader() {
        homeViewModel.loader.observe(this as LifecycleOwner) { Loader ->
            when (Loader) {
                true -> homeFragmentBinding.progressBar.visibility = View.VISIBLE
                else -> homeFragmentBinding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun showUnitsDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Select Temperature Unit")
            .setSingleChoiceItems(options, -1) { dialog_, which ->
                selectedChoice = options[which]
            }
            .setPositiveButton("Ok") { dialog, which ->
                when (selectedChoice) {
                    "Celsius" -> {
                        homeViewModel.getWeatherDetails("metric")
                        sharedPreferenceHelper.saveUnit("°C")
                    }
                    "Fahrenheit" -> {
                        homeViewModel.getWeatherDetails("imperial")
                        sharedPreferenceHelper.saveUnit("°F")
                    }
                }
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }


}