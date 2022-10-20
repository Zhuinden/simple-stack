package com.community.simplestackkotlindaggerexample.screens.home

import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import kotlinx.parcelize.Parcelize

@Parcelize
data object HomeKey : DefaultFragmentKey() {
    override fun instantiateFragment() = HomeFragment()
}