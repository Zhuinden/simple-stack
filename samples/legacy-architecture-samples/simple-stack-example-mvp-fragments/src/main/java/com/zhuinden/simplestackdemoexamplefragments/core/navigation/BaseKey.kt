package com.zhuinden.simplestackdemoexamplefragments.core.navigation

/**
 * Created by Zhuinden on 2018. 08. 20.
 */

abstract class BaseKey : FragmentKey() {
    override fun getFragmentTag(): String = toString()
}
