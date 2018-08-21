package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.addoredittask

import android.support.v4.app.Fragment
import android.view.View
import com.zhuinden.simplestackdemoexamplefragments.R
import com.zhuinden.simplestackdemoexamplefragments.application.BaseKey
import com.zhuinden.simplestackdemoexamplefragments.application.Key
import kotlinx.android.parcel.Parcelize

/**
 * Created by Zhuinden on 2018. 08. 20.
 */
sealed class AddOrEditTaskKey(val parent: Key, val taskId: String = "") : BaseKey() {
    constructor(parent: Key) : this(parent, "")

    @Parcelize
    data class AddTaskKey(val parentKey: Key): AddOrEditTaskKey(parentKey)

    @Parcelize
    data class EditTaskKey(val parentKey: Key, val taskID: String): AddOrEditTaskKey(parentKey, taskID)

    override fun layout(): Int = R.layout.path_addoredittask

    override val isFabVisible: Boolean
        get() = true

    override fun createFragment(): Fragment = AddOrEditTaskFragment()

    override fun menu(): Int = R.menu.empty_menu

    override fun navigationViewId(): Int = 0

    override fun shouldShowUp(): Boolean = true

    override fun fabClickListener(fragment: Fragment): View.OnClickListener =
        View.OnClickListener { v ->
            val addOrEditTaskFragment = fragment as AddOrEditTaskFragment
            if (addOrEditTaskFragment.saveTask()) {
                addOrEditTaskFragment.navigateBack()
            }
        }


    override fun fabDrawableIcon(): Int = R.drawable.ic_done
}
