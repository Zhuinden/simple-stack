package com.zhuinden.navigationexamplekotlinview.screens

import com.zhuinden.navigationexamplekotlinview.R
import com.zhuinden.navigationexamplekotlinview.core.navigation.BaseKey
import kotlinx.parcelize.Parcelize

/**
 * Created by Owner on 2017. 06. 29..
 */
@Parcelize
data object DashboardKey : BaseKey() {
    override fun layout(): Int = R.layout.dashboard_view
}
