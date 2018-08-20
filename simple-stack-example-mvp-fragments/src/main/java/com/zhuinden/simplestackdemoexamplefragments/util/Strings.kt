package com.zhuinden.simplestackdemoexamplefragments.util

/**
 * Created by Owner on 2017. 01. 27..
 */

object Strings {
    @JvmStatic
    fun isNullOrEmpty(string: String?): Boolean {
        return string == null || string.isEmpty()
    }
}
