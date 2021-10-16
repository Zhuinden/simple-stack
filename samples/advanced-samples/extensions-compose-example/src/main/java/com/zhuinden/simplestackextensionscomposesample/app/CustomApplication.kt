package com.zhuinden.simplestackextensionscomposesample.app

import android.app.Application
import android.preference.PreferenceManager
import com.zhuinden.simplestack.GlobalServices
import com.zhuinden.simplestackextensions.servicesktx.add

class CustomApplication : Application() {
    lateinit var globalServices: GlobalServices
        private set

    override fun onCreate() {
        super.onCreate()

        @Suppress("DEPRECATION")
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        val authenticationManager = AuthenticationManager(sharedPreferences)

        globalServices = GlobalServices.builder()
            .add(authenticationManager)
            .build()
    }
}