package com.zhuinden.simplestackexamplescoping

import android.os.Bundle
import android.os.Parcelable

/**
 * Created by Zhuinden on 2018.09.17.
 */
interface BaseKey : Parcelable {
    val fragmentTag: String
        get() = toString()

    fun newInstance(): BaseFragment = createFragment().apply {
        arguments = (arguments ?: Bundle()).also { bundle ->
            bundle.putParcelable("KEY", this@BaseKey)
        }
    }

    fun createFragment(): BaseFragment
}
