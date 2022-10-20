package com.community.simplestackkotlindaggerexample.screens.users

import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import kotlinx.parcelize.Parcelize

@Parcelize
data object UsersKey : DefaultFragmentKey() {
    override fun instantiateFragment() = UsersFragment()
}