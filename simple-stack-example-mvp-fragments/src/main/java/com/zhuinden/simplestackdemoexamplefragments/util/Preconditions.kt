package com.zhuinden.simplestackdemoexamplefragments.util

/**
 * Created by Zhuinden on 2018. 08. 20.
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
