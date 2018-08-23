package com.zhuinden.simplestackkotlindaggerexample

import kotlinx.android.parcel.Parcelize

@Parcelize
data class HomeKey(val tag: String) : BaseKey() {
    constructor() : this("HomeKey")

    override fun createFragment() = HomeFragment()
}