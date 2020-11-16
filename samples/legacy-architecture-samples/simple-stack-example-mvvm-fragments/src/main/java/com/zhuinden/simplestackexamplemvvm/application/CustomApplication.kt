package com.zhuinden.simplestackexamplemvvm.application


import android.app.Application
import com.zhuinden.simplestackexamplemvvm.application.injection.ApplicationComponent
import com.zhuinden.simplestackexamplemvvm.application.injection.DaggerApplicationComponent

/**
 * Created by Zhuinden on 2017.07.26..
 */
class CustomApplication : Application() {
    var applicationComponent: ApplicationComponent? = null

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        applicationComponent = DaggerApplicationComponent.factory().create(this)
    }

    fun appComponent(): ApplicationComponent {
        return applicationComponent!!
    }

    companion object {
        var INSTANCE: CustomApplication? = null
            private set
    }
}