package com.zhuinden.simplestackexamplekotlin

import android.os.Bundle
import android.os.Parcelable

/**
 * Created by Owner on 2017.11.13.
 */
abstract class BaseKey : Parcelable {
    val fragmentTag: String
        get() = toString()

    fun newFragment(): BaseFragment = createFragment().apply {
        arguments = (arguments ?: Bundle()).also { bundle ->
            bundle.putParcelable("KEY", this@BaseKey)
        }
    }

    protected abstract fun createFragment(): BaseFragment
}
