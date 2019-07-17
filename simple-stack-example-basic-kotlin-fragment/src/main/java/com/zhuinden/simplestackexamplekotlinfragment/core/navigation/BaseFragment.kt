package com.zhuinden.simplestackexamplekotlinfragment.core.navigation

import android.support.v4.app.Fragment
import com.zhuinden.utils.requireArguments

/**
 * Created by Owner on 2017.11.13.
 */

open class BaseFragment : Fragment() {
    fun <T : BaseKey> getKey(): T = requireArguments.getParcelable<T>("KEY")!!
}
