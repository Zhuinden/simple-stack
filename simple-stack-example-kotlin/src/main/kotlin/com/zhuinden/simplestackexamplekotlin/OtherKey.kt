package com.zhuinden.simplestackexamplekotlin

import android.annotation.SuppressLint
import kotlinx.android.parcel.Parcelize

/**
 * Created by Owner on 2017.11.13.
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class OtherKey(val tag: String) : BaseKey() {
    constructor() : this("OtherKey")

    override fun createFragment(): BaseFragment = OtherFragment()
}
