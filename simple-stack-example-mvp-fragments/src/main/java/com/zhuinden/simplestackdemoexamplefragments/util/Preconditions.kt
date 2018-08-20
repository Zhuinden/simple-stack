package com.zhuinden.simplestackdemoexamplefragments.util

/**
 * Created by Owner on 2017. 01. 26..
 */

object Preconditions {
    @JvmStatic
    fun <T> checkNotNull(reference: T?): T = when {
        reference == null -> throw NullPointerException()
        else -> reference
    }

    @JvmStatic
    fun <T> checkNotNull(reference: T?, message: String): T =
        reference ?: throw NullPointerException(message)
}
