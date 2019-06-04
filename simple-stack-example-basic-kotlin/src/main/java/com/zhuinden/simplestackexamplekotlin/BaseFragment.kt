package com.zhuinden.simplestackexamplekotlin

import android.support.v4.app.Fragment

/**
 * Created by Owner on 2017.11.13.
 */

open class BaseFragment : Fragment() {
    fun <T : BaseKey> getKey(): T = requireArguments.getParcelable<T>("KEY")
}
