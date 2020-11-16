package com.zhuinden.simplestackexamplemvvm.application.injection


import com.zhuinden.simplestackexamplemvvm.application.CustomApplication

/**
 * Created by Zhuinden on 2017.07.25..
 */
object Injector {
    fun get(): ApplicationComponent {
        return CustomApplication.INSTANCE!!.appComponent()
    }
}