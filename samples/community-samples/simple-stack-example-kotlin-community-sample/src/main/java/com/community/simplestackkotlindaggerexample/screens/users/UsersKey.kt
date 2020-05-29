package com.community.simplestackkotlindaggerexample.screens.users

import com.community.simplestackkotlindaggerexample.core.navigation.BaseKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UsersKey(private val placeholder: String = "") : BaseKey() {
    override fun createFragment() = UsersFragment()
}