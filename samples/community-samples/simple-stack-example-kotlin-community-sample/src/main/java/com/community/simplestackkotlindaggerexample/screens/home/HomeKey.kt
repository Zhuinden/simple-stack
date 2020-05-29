package com.community.simplestackkotlindaggerexample.screens.home

import com.community.simplestackkotlindaggerexample.core.navigation.BaseKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HomeKey(private val placeholder: String = "") : BaseKey() {
    override fun createFragment() = HomeFragment()
}