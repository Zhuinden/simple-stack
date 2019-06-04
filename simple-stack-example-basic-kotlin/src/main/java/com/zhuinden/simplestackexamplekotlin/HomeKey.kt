package com.zhuinden.simplestackexamplekotlin

import android.annotation.SuppressLint
import kotlinx.android.parcel.Parcelize

/**
 * Created by Owner on 2017.11.13.
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class HomeKey(val tag: String) : BaseKey() {
    constructor() : this("HomeKey")

    override fun createFragment() = HomeFragment()
}
