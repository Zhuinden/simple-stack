package com.zhuinden.simplestacktutorials.steps.step_6

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment

abstract class Step6Screen : Parcelable {
    open val fragmentTag: String
        get() = toString()

    protected abstract fun instantiateFragment(): Fragment

    fun createFragment(): Fragment = instantiateFragment().apply {
        arguments = (arguments ?: Bundle()).also { bundle ->
            bundle.putParcelable("FRAGMENT_KEY", this@Step6Screen)
        }
    }
}