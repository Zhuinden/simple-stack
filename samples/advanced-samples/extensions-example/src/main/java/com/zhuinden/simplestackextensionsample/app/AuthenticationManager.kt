package com.zhuinden.simplestackextensionsample.app

import android.content.SharedPreferences

class AuthenticationManager(
    private val sharedPreferences: SharedPreferences
) {
    companion object {
        private const val KEY_IS_REGISTERED = "isRegistered"
        private const val KEY_REGISTERED_USERNAME = "registeredUsername"
    }

    fun isAuthenticated(): Boolean =
        sharedPreferences.getBoolean(KEY_IS_REGISTERED, false)

    fun saveRegistration(username: String) {
        sharedPreferences.edit()
            .putBoolean(KEY_IS_REGISTERED, true)
            .putString(KEY_REGISTERED_USERNAME, username)
            .apply()
    }

    fun clearRegistration() {
        sharedPreferences.edit()
            .remove(KEY_IS_REGISTERED)
            .remove(KEY_REGISTERED_USERNAME)
            .apply()
    }

    fun getAuthenticatedUser(): String {
        val username = sharedPreferences.getString(KEY_REGISTERED_USERNAME, "").takeUnless { it.isNullOrEmpty() }

        return checkNotNull(username)
    }
}