package com.community.simplestackkotlindaggerexample.core.navigation

import android.support.v4.app.Fragment
import com.community.simplestackkotlindaggerexample.utils.requireArguments

open class BaseFragment : Fragment() {
    fun <T : BaseKey> getKey(): T = requireArguments.getParcelable<T>("KEY")
}