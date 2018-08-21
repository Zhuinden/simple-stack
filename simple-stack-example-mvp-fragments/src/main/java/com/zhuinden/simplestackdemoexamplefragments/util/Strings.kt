package com.zhuinden.simplestackdemoexamplefragments.util

/**
 * Created by Zhuinden on 2018. 08. 20.
 */

object Strings {
    @JvmStatic
    fun isNullOrEmpty(string: String?): Boolean {
        return string == null || string.isEmpty()
    }
}
