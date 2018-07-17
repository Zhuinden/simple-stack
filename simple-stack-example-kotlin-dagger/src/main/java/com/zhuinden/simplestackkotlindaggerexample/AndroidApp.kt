package com.zhuinden.simplestackkotlindaggerexample

import android.content.Context
import android.support.multidex.MultiDexApplication

import io.realm.Realm

class AndroidApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(applicationContext))
            .build()
        Realm.init(this)
    }

    companion object {
        lateinit var appComponent: AppComponent
            private set
        private lateinit var INSTANCE: AndroidApp

        operator fun get(context: Context): AndroidApp {
            return context.applicationContext as AndroidApp
        }

        fun get(): AndroidApp {
            return INSTANCE
        }
    }
}
