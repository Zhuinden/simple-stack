package com.zhuinden.simplestacktutorials.steps.step_6

import androidx.fragment.app.Fragment

abstract class Step6BaseFragment : Fragment() {
    fun <T : Step6Screen> getScreen(): T = requireArguments().getParcelable<T>("FRAGMENT_KEY")!!
}