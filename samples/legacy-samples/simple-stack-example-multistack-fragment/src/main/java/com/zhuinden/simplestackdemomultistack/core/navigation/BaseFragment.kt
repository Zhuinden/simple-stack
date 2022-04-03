package com.zhuinden.simplestackdemomultistack.core.navigation

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

/**
 * Created by Zhuinden on 2018.09.17.
 */

open class BaseFragment(@LayoutRes layoutId: Int) : Fragment(layoutId) {
    fun <T : MultistackFragmentKey> getKey(): T = requireArguments().getParcelable<T>("KEY")!!
}
