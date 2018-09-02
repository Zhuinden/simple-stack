package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.addoredittask

import android.support.v4.app.Fragment
import android.view.View
import com.zhuinden.simplestack.ScopedServices
import com.zhuinden.simplestackdemoexamplefragments.R
import com.zhuinden.simplestackdemoexamplefragments.application.BaseKey
import com.zhuinden.simplestackdemoexamplefragments.application.Injector
import com.zhuinden.simplestackdemoexamplefragments.application.Key
import com.zhuinden.simplestackdemoexamplefragments.util.scopedservices.HasServices
import kotlinx.android.parcel.Parcelize

/**
 * Created by Zhuinden on 2018. 08. 20.
 */
sealed class AddOrEditTaskKey(val parent: Key, val taskId: String = "") : BaseKey(), HasServices {
    override fun bindServices(serviceBinder: ScopedServices.ServiceBinder) {
        serviceBinder.add(AddOrEditTaskFragment.CONTROLLER_TAG, Injector.get().addOrEditTaskPresenter())
    }

    constructor(parent: Key) : this(parent, "")

    @Parcelize
    data class AddTaskKey(val parentKey: Key): AddOrEditTaskKey(parentKey) {
        override fun getScopeTag(): String = "AddTask"
    }

    @Parcelize
    data class EditTaskKey(val parentKey: Key, val taskID: String): AddOrEditTaskKey(parentKey, taskID) {
        override fun getScopeTag(): String = "EditTask[$taskID]"
    }

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
            addOrEditTaskFragment.onSaveButtonClicked()
        }


    override fun fabDrawableIcon(): Int = R.drawable.ic_done
}
