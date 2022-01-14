package com.example.weatherapp.common

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.core.content.edit


class SharedPreferenceHelper {

    companion object {

        private const val UNIT = "Unit"
        private var prefs: SharedPreferences? = null

        @Volatile
        private var instance: SharedPreferenceHelper? = null

        fun getInstance(context: Context): SharedPreferenceHelper {
            synchronized(this) {
                val _instance = instance
                if (_instance == null) {
                    prefs = PreferenceManager.getDefaultSharedPreferences(context)
                    instance = _instance
                }
                return SharedPreferenceHelper()
            }
        }
    }

    fun saveUnit(unit: String) {
        prefs?.edit(commit = true) {
            putString(UNIT, unit)
        }
    }


    fun getSelectedTemperatureUnit() = prefs?.getString(UNIT, "")

}