package com.zhuinden.simplestackexamplekotlin

import android.os.Bundle
import paperparcel.PaperParcelable

/**
 * Created by Owner on 2017.11.13.
 */
abstract class BaseKey : PaperParcelable {
    val fragmentTag: String
        get() = toString()

    fun newFragment(): BaseFragment {
        val fragment = createFragment()
        var bundle: Bundle? = fragment.arguments
        if (bundle == null) {
            bundle = Bundle()
        }
        bundle.putParcelable("KEY", this)
        fragment.arguments = bundle
        return fragment
    }

    protected abstract fun createFragment(): BaseFragment
}
