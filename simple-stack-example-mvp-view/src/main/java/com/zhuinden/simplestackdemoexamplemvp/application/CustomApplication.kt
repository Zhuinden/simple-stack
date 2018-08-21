package com.zhuinden.simplestackdemoexamplemvp.application

import android.app.Application
import android.content.Context

import com.zhuinden.simplestackdemoexamplemvp.application.injection.DaggerSingletonComponent
import com.zhuinden.simplestackdemoexamplemvp.application.injection.SingletonComponent

/**
 * Created by Owner on 2017. 01. 26..
 */

class CustomApplication : Application() {
    private var component: SingletonComponent? = null
    fun component() = component!!

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        component = DaggerSingletonComponent.create()
    }

    companion object {
        private var INSTANCE: CustomApplication? = null

        operator fun get(context: Context): CustomApplication =
            context.applicationContext as CustomApplication

        fun get(): CustomApplication = INSTANCE!!
    }
}
