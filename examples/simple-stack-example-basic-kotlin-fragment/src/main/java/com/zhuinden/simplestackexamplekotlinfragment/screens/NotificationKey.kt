package com.zhuinden.simplestackexamplekotlinfragment.screens

import com.zhuinden.simplestackexamplekotlinfragment.core.navigation.BaseFragment
import com.zhuinden.simplestackexamplekotlinfragment.core.navigation.BaseKey
import kotlinx.android.parcel.Parcelize

/**
 * Created by Owner on 2017.11.13.
 */
@Parcelize
data class NotificationKey(private val placeholder: String = "") : BaseKey() { // generate reliable `toString()` for no-args data class
    override fun createFragment(): BaseFragment = NotificationFragment()
}
