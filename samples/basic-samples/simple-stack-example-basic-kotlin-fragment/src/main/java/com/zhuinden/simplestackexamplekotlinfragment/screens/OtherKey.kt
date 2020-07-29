package com.zhuinden.simplestackexamplekotlinfragment.screens

import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import kotlinx.android.parcel.Parcelize

/**
 * Created by Owner on 2017.11.13.
 */
@Parcelize
data class OtherKey(private val placeholder: String = "") : DefaultFragmentKey() { // generate reliable `toString()` for no-args data class
    override fun instantiateFragment() = OtherFragment()
}
