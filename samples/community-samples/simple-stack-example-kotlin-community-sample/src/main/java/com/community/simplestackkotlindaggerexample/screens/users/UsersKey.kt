package com.community.simplestackkotlindaggerexample.screens.users

import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UsersKey(private val placeholder: String = "") : DefaultFragmentKey() {
    override fun instantiateFragment() = UsersFragment()
}