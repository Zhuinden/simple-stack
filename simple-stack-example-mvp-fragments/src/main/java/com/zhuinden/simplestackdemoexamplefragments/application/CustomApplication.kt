package com.zhuinden.simplestackdemoexamplefragments.application

import android.app.Application
import android.content.Context

import com.zhuinden.simplestackdemoexamplefragments.application.injection.DaggerSingletonComponent
import com.zhuinden.simplestackdemoexamplefragments.application.injection.SingletonComponent

/**
 * Created by Zhuinden on 2018. 08. 20.
 */

class CustomApplication : Application() {
    companion object {
        private var INSTANCE: CustomApplication? = null

        private var component: SingletonComponent? = null

        @JvmStatic
        fun getComponent() = component!!

        @JvmStatic
        fun get(context: Context): CustomApplication {
            return context.applicationContext as CustomApplication
        }

        @JvmStatic
        fun get(): CustomApplication = INSTANCE!!
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        component = DaggerSingletonComponent.create()
    }
}
