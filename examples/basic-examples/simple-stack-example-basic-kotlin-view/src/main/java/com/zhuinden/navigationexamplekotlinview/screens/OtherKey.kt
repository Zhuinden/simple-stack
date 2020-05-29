package com.zhuinden.navigationexamplekotlinview.screens

import com.zhuinden.navigationexamplekotlinview.R
import com.zhuinden.navigationexamplekotlinview.core.navigation.BaseKey
import com.zhuinden.simplestack.navigator.ViewChangeHandler
import com.zhuinden.simplestack.navigator.changehandlers.SegueViewChangeHandler
import kotlinx.android.parcel.Parcelize

/**
 * Created by Owner on 2017. 06. 29..
 */
@Parcelize
data class OtherKey(private val placeholder: String = "") : BaseKey() {
    override fun layout(): Int = R.layout.other_view

    override fun viewChangeHandler(): ViewChangeHandler = SegueViewChangeHandler()
}