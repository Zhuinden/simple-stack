package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.statistics

import android.view.View
import com.zhuinden.simplestack.navigator.ViewChangeHandler
import com.zhuinden.simplestack.navigator.changehandlers.SegueViewChangeHandler
import com.zhuinden.simplestackdemoexamplemvp.R
import com.zhuinden.simplestackdemoexamplemvp.application.Key
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StatisticsKey(val placeholder: String) : Key {
    constructor() : this("")

    override fun layout(): Int = R.layout.path_statistics

    override val isFabVisible: Boolean
        get() = false

    override fun viewChangeHandler(): ViewChangeHandler = SegueViewChangeHandler()

    override fun menu(): Int = R.menu.empty_menu

    override fun navigationViewId(): Int = R.id.statistics_navigation_menu_item

    override fun shouldShowUp(): Boolean = false

    override fun fabClickListener(view: View): View.OnClickListener = View.OnClickListener { v -> }

    override fun fabDrawableIcon(): Int = 0
}
