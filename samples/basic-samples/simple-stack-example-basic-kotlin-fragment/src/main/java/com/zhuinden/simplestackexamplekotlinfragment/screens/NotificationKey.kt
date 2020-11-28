package com.zhuinden.simplestackexamplekotlinfragment.screens

import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import kotlinx.parcelize.Parcelize

/**
 * Created by Owner on 2017.11.13.
 */
@Parcelize
data class NotificationKey(private val placeholder: String = "") : DefaultFragmentKey() { // generate reliable `toString()` for no-args data class
    override fun instantiateFragment() = NotificationFragment()
}
