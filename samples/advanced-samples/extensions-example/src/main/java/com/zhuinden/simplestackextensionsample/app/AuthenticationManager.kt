package com.zhuinden.simplestackextensionsample.app

import android.content.Context
import android.preference.PreferenceManager

object AuthenticationManager {
    @Suppress("DEPRECATION") // w/e androidx-chan
    fun isAuthenticated(appContext: Context): Boolean {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(appContext)
        return sharedPref.getBoolean("isRegistered", false)
    }

    @Suppress("DEPRECATION") // w/e androidx-chan
    fun saveRegistration(appContext: Context) {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(appContext)
        sharedPref.edit().putBoolean("isRegistered", true).apply()
    }

    @Suppress("DEPRECATION") // w/e androidx-chan
    fun clearRegistration(appContext: Context) {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(appContext)
        sharedPref.edit().remove("isRegistered").apply()
    }

    var authToken: String = "" // why would this be in the viewModel?
}