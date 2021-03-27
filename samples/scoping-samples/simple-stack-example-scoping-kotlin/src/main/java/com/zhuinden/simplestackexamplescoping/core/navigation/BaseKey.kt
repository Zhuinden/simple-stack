package com.zhuinden.simplestackexamplescoping.core.navigation

import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider

/**
 * Created by Zhuinden on 2018.09.17.
 */
abstract class BaseKey : DefaultFragmentKey(), DefaultServiceProvider.HasServices {
    override fun getFragmentTag(): String = toString()

    override fun getScopeTag(): String = fragmentTag
}
