package com.zhuinden.simplestackexamplekotlinfragment.core.navigation

import androidx.fragment.app.Fragment
import com.zhuinden.simplestackexamplekotlinfragment.utils.requireArguments

/**
 * Created by Owner on 2017.11.13.
 */

open class BaseFragment : Fragment() {
    fun <T : BaseKey> getKey(): T = requireArguments.getParcelable<T>("KEY")!!
}
