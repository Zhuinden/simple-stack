package com.zhuinden.simplestackexamplemvvm.application


import android.app.Application
import com.zhuinden.simplestack.GlobalServices
import com.zhuinden.simplestackexamplemvvm.application.injection.DaggerApplicationComponent
import com.zhuinden.simplestackextensions.servicesktx.add

/**
 * Created by Zhuinden on 2017.07.26..
 */
class CustomApplication : Application() {
    lateinit var globalServices: GlobalServices
        private set

    override fun onCreate() {
        super.onCreate()

        val applicationComponent = DaggerApplicationComponent.factory().create(this)

        globalServices = GlobalServices.builder()
            .add(applicationComponent)
            .build()
    }
}