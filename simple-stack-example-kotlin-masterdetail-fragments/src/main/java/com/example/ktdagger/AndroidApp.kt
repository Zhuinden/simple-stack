package com.example.ktdagger

import android.content.Context
import android.support.multidex.MultiDexApplication

import io.realm.Realm

class AndroidApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        if (INSTANCE == null) {
            INSTANCE = this
            appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(applicationContext))
                .build()
        }
        Realm.init(this)
    }

    companion object {
        var appComponent: AppComponent? = null
            private set
        private var INSTANCE: AndroidApp? = null

        operator fun get(context: Context): AndroidApp {
            return context.applicationContext as AndroidApp
        }

        fun get(): AndroidApp? {
            return INSTANCE
        }
    }
}
