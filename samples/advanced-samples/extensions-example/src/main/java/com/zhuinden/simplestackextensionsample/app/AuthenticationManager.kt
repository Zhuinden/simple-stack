package com.zhuinden.simplestackextensionsample.app

import android.content.SharedPreferences

class AuthenticationManager(
    private val sharedPreferences: SharedPreferences
) {
    fun isAuthenticated(): Boolean {
        return sharedPreferences.getBoolean("isRegistered", false)
    }

    fun saveRegistration() {
        sharedPreferences.edit().putBoolean("isRegistered", true).apply()
    }

    fun clearRegistration() {
        sharedPreferences.edit().remove("isRegistered").apply()
    }

    var authToken: String = "" // why would this be in the viewModel?
}