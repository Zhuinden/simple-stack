package com.community.simplestackkotlindaggerexample.core.navigation

import androidx.fragment.app.Fragment
import com.community.simplestackkotlindaggerexample.utils.requireArguments

open class BaseFragment : Fragment() {
    fun <T : BaseKey> getKey(): T = requireArguments.getParcelable<T>("KEY")
}