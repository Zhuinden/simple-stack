package com.community.simplestackkotlindaggerexample.screens.userdetail

import com.community.simplestackkotlindaggerexample.data.database.User
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserDetailKey(val user: User) : DefaultFragmentKey() {
    override fun instantiateFragment() = UserDetailFragment()

    override fun toString(): String = "UserDetailKey[${user.userId}]"
}