package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.tasks

import android.support.v4.app.Fragment
import android.view.View
import com.zhuinden.simplestackdemoexamplefragments.R
import com.zhuinden.simplestackdemoexamplefragments.application.BaseKey
import kotlinx.android.parcel.Parcelize

/**
 * Created by Zhuinden on 2018. 08. 20.
 */

@Parcelize
data class TasksKey(val placeholder: String = "") : BaseKey() {
    constructor() : this("")

    override fun layout(): Int = R.layout.path_tasks

    override val isFabVisible: Boolean
        get() = true

    override fun createFragment(): Fragment = TasksFragment()

    override fun menu(): Int = R.menu.tasks_fragment_menu

    override fun navigationViewId(): Int = R.id.list_navigation_menu_item

    override fun shouldShowUp(): Boolean = false

    override fun fabClickListener(f: Fragment): View.OnClickListener =
        View.OnClickListener { v ->
            val fragment = f as TasksFragment
            fragment.openAddNewTask()
        }

    override fun fabDrawableIcon(): Int = R.drawable.ic_add
}
