package com.zhuinden.simplestackexamplescoping

import android.support.v4.app.Fragment

/**
 * Created by Zhuinden on 2018.09.17.
 */

open class BaseFragment : Fragment() {
    fun <T : BaseKey> getKey(): T = requireArguments.getParcelable<T>("KEY")
}
