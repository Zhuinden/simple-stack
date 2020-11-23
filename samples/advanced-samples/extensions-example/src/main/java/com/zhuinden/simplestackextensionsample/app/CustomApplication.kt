package com.zhuinden.simplestackextensionsample.app

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
        val authenticationManager = AuthenticationManager(
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        )

        globalServices = GlobalServices.builder()
            .add(authenticationManager)
            .build()
    }
}