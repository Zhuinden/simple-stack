package com.zhuinden.simplestacktutorials.steps.step_8.core.navigation

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment

abstract class FragmentKey: Parcelable {
    open val fragmentTag: String
        get() = toString()

    protected abstract fun instantiateFragment(): Fragment

    fun createFragment(): Fragment = instantiateFragment().apply {
        arguments = (arguments ?: Bundle()).also { bundle ->
            bundle.putParcelable("FRAGMENT_KEY", this@FragmentKey)
        }
    }
}