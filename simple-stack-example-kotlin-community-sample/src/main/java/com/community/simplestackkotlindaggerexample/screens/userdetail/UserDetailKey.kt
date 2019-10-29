package com.community.simplestackkotlindaggerexample.screens.userdetail

import com.community.simplestackkotlindaggerexample.core.navigation.BaseKey
import com.community.simplestackkotlindaggerexample.data.database.User
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserDetailKey(val user: User) : BaseKey() {
    override fun createFragment() = UserDetailFragment()

    override fun toString(): String = "UserDetailKey[${user.userId}]"
}