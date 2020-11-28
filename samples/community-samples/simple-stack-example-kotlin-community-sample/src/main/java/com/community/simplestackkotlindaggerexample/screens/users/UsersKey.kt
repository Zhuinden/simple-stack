package com.community.simplestackkotlindaggerexample.screens.users

import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class UsersKey(private val placeholder: String = "") : DefaultFragmentKey() {
    override fun instantiateFragment() = UsersFragment()
}