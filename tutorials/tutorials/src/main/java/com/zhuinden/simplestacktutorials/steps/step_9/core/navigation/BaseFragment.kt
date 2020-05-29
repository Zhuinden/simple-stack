package com.zhuinden.simplestacktutorials.steps.step_9.core.navigation

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

abstract class BaseFragment(@LayoutRes layoutId: Int) : Fragment(layoutId) {
    fun <T : FragmentKey> getScreen(): T = requireArguments().getParcelable<T>("FRAGMENT_KEY")!!
}