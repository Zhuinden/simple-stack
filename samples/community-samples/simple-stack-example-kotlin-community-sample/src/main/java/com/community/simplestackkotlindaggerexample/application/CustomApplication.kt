package com.community.simplestackkotlindaggerexample.application

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.community.simplestackkotlindaggerexample.application.injection.AppComponent
import com.community.simplestackkotlindaggerexample.application.injection.AppModule
import com.community.simplestackkotlindaggerexample.application.injection.DaggerAppComponent

import io.realm.Realm

class CustomApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        INSTANCE = this

        Injector.appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(applicationContext))
            .build()
        Realm.init(this)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    companion object {
        private lateinit var INSTANCE: CustomApplication

        @JvmStatic
        fun get(): CustomApplication = INSTANCE
    }
}

object Injector {
    lateinit var appComponent: AppComponent

    fun get() = appComponent
}
