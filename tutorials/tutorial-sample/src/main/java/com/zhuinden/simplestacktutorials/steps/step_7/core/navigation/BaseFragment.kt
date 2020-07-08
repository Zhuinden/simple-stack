package com.zhuinden.simplestacktutorials.steps.step_7.core.navigation

import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {
    fun <T : FragmentKey> getScreen(): T = requireArguments().getParcelable<T>("FRAGMENT_KEY")!!
}