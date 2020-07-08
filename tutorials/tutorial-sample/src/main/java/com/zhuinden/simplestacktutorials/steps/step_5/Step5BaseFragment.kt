package com.zhuinden.simplestacktutorials.steps.step_5

import androidx.fragment.app.Fragment

abstract class Step5BaseFragment : Fragment() {
    fun <T : Step5Screen> getScreen(): T = requireArguments().getParcelable<T>("FRAGMENT_KEY")!!
}