package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.statistics

import android.support.v4.app.Fragment
import android.view.View
import com.zhuinden.simplestackdemoexamplefragments.R
import com.zhuinden.simplestackdemoexamplefragments.application.BaseKey
import kotlinx.android.parcel.Parcelize

/**
 * Created by Zhuinden on 2018. 08. 20.
 */
@Parcelize
data class StatisticsKey(val placeholder: String = "") : BaseKey() {
    constructor() : this("")

    override fun layout(): Int = R.layout.path_statistics

    override val isFabVisible: Boolean
        get() = false

    override fun createFragment(): Fragment = StatisticsFragment()

    override fun menu(): Int = R.menu.empty_menu

    override fun navigationViewId(): Int = R.id.statistics_navigation_menu_item

    override fun shouldShowUp(): Boolean = false

    override fun fabClickListener(fragment: Fragment): View.OnClickListener =
        View.OnClickListener { v ->

        }

    override fun fabDrawableIcon(): Int = 0
}
