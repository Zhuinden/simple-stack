package com.zhuinden.simplestackkotlindaggerexample

import android.content.Context
import android.support.multidex.MultiDexApplication

import io.realm.Realm

class CustomApplication : MultiDexApplication() {

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

        private lateinit var INSTANCE: CustomApplication

        operator fun get(context: Context): CustomApplication {
            return context.applicationContext as CustomApplication
        }

        fun get(): CustomApplication {
            return INSTANCE
        }
    }
}
