package com.zhuinden.navigationexamplekotlinview.screens

import com.zhuinden.navigationexamplekotlinview.R
import com.zhuinden.navigationexamplekotlinview.core.navigation.BaseKey
import kotlinx.android.parcel.Parcelize

/**
 * Created by Owner on 2017. 06. 29..
 */
@Parcelize
data class HomeKey(private val placeholder: String = "") : BaseKey() {
    override fun layout(): Int = R.layout.home_view
}
