package com.zhuinden.simplestackextensionsample.app

import android.app.Application
import android.preference.PreferenceManager

class CustomApplication: Application() {
    lateinit var authenticationManager: AuthenticationManager
        private set

    override fun onCreate() {
        super.onCreate()

        @Suppress("DEPRECATION")
        authenticationManager = AuthenticationManager(PreferenceManager.getDefaultSharedPreferences(this))
    }
}