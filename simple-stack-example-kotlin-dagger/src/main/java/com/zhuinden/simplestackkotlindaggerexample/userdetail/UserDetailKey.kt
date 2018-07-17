package com.zhuinden.simplestackkotlindaggerexample.userdetail

import com.zhuinden.simplestackkotlindaggerexample.BaseKey
import com.zhuinden.simplestackkotlindaggerexample.realmobjects.UserRO
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserDetailKey(
    val tag: String,
    val userRO: UserRO
) : BaseKey() {

    constructor(userRO: UserRO) : this("UserDetailKey", userRO)

    override fun createFragment() = UserDetailFragment.newInstance(userRO)
}