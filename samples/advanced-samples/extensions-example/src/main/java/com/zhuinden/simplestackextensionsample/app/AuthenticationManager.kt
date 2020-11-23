package com.zhuinden.simplestackextensionsample.app

import android.content.SharedPreferences

class AuthenticationManager(
    private val sharedPreferences: SharedPreferences
) {
    companion object {
        private const val KEY_IS_REGISTERED = "isRegistered"
    }

    fun isAuthenticated(): Boolean =
        sharedPreferences.getBoolean(KEY_IS_REGISTERED, false)


    fun saveRegistration() {
        sharedPreferences.edit().putBoolean(KEY_IS_REGISTERED, true).apply()
    }

    fun clearRegistration() {
        sharedPreferences.edit().remove(KEY_IS_REGISTERED).apply()
    }
}