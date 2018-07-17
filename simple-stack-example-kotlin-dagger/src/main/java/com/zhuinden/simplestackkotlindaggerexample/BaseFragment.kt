package com.zhuinden.simplestackkotlindaggerexample

import android.support.v4.app.Fragment

open class BaseFragment : Fragment() {
    fun <T : BaseKey> getKey(): T = requireArguments.getParcelable<T>("KEY")
}