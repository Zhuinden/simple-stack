package com.zhuinden.simplestackkotlindaggerexample.userdetail

import com.zhuinden.simplestackkotlindaggerexample.BaseKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UsersKey(val tag: String) : BaseKey() {
    constructor() : this("UsersKey")

    override fun createFragment() = UsersFragment()
}