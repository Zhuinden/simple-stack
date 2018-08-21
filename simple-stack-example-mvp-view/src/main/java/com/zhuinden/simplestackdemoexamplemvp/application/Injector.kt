package com.zhuinden.simplestackdemoexamplemvp.application

import com.zhuinden.simplestackdemoexamplemvp.application.injection.SingletonComponent

/**
 * Created by Zhuinden on 2017.03.27..
 */

object Injector {
    @JvmStatic
    fun get(): SingletonComponent = CustomApplication.get().component()
}
