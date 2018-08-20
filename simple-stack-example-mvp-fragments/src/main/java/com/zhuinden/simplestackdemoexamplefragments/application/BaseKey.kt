package com.zhuinden.simplestackdemoexamplefragments.application

import android.os.Bundle
import android.support.v4.app.Fragment

import com.zhuinden.simplestackdemoexamplefragments.util.BaseFragment

/**
 * Created by Owner on 2017. 02. 03..
 */

abstract class BaseKey : Key {
    override fun newFragment(): Fragment = createFragment().also { fragment ->
        fragment.arguments = (fragment.arguments ?: Bundle()).also { bundle ->
            bundle.putParcelable(BaseFragment.KEY_TAG, this)
        }
    }

    protected abstract fun createFragment(): Fragment

    override val fragmentTag: String
        get() = toString()
}
