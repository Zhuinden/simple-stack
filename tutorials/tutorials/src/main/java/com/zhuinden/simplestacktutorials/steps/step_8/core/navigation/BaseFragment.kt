package com.zhuinden.simplestacktutorials.steps.step_8.core.navigation

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

abstract class BaseFragment(@LayoutRes layoutRes: Int) : Fragment(layoutRes) {
    fun <T : FragmentKey> getScreen(): T = requireArguments().getParcelable<T>("FRAGMENT_KEY")!!
}