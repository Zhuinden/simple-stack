package com.zhuinden.simplestackexamplekotlinfragment.screens

import com.zhuinden.simplestackexamplekotlinfragment.core.navigation.BaseKey
import kotlinx.android.parcel.Parcelize

/**
 * Created by Owner on 2017.11.13.
 */
@Parcelize
data class HomeKey(private val placeholder: String = "") : BaseKey() { // generate reliable `toString()` for no-args data class
    override fun createFragment() = HomeFragment()
}
