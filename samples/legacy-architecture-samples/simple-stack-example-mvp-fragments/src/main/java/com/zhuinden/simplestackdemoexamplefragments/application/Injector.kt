package com.zhuinden.simplestackdemoexamplefragments.application

import com.zhuinden.simplestackdemoexamplefragments.application.injection.SingletonComponent

/**
 * Created by Zhuinden on 2018. 08. 19..
 */
object Injector {
    @JvmStatic
    fun get(): SingletonComponent = CustomApplication.getComponent()
}

