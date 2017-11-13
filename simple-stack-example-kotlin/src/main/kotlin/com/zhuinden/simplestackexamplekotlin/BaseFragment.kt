package com.zhuinden.simplestackexamplekotlin

import android.support.v4.app.Fragment

/**
 * Created by Owner on 2017. 06. 29..
 */

open class BaseFragment : Fragment() {
    fun <T : BaseKey> getKey(): T? = arguments?.getParcelable<T>("KEY")
}
