package com.zhuinden.simplestackdemoexamplefragments.application

import android.app.Application
import android.content.Context

import com.zhuinden.simplestackdemoexamplefragments.application.injection.DaggerSingletonComponent
import com.zhuinden.simplestackdemoexamplefragments.application.injection.SingletonComponent

/**
 * Created by Zhuinden on 2018. 08. 20.
 */

class CustomApplication : Application() {
    private var component: SingletonComponent? = null

    companion object {
        private var INSTANCE: CustomApplication? = null

        @JvmStatic
        fun getComponent() = get().component!!

        @JvmStatic
        fun get(context: Context): CustomApplication =
            context.applicationContext as CustomApplication

        @JvmStatic
        fun get(): CustomApplication = INSTANCE!!
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        component = DaggerSingletonComponent.create()
    }
}
