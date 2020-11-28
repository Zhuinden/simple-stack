package com.zhuinden.simplestacktutorials.steps.step_5

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

abstract class Step5BaseFragment(@LayoutRes layoutId: Int) : Fragment(layoutId) {
    fun <T : Step5Screen> getScreen(): T = requireArguments().getParcelable<T>("FRAGMENT_KEY")!!
}