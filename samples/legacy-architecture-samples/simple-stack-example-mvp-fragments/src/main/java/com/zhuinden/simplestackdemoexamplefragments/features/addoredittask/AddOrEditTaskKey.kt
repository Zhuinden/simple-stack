package com.zhuinden.simplestackdemoexamplefragments.features.addoredittask

import android.view.View
import androidx.fragment.app.Fragment
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackdemoexamplefragments.R
import com.zhuinden.simplestackdemoexamplefragments.core.navigation.BaseKey
import com.zhuinden.simplestackdemoexamplefragments.core.navigation.FragmentKey
import com.zhuinden.simplestackdemoexamplefragments.data.repository.TaskRepository
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider
import com.zhuinden.simplestackextensions.servicesktx.lookup
import kotlinx.parcelize.Parcelize

/**
 * Created by Zhuinden on 2018. 08. 20.
 */
sealed class AddOrEditTaskKey(val parent: FragmentKey, val taskId: String = "") : BaseKey(), DefaultServiceProvider.HasServices {
    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            addService(AddOrEditTaskFragment.CONTROLLER_TAG, AddOrEditTaskPresenter(
                lookup<TaskRepository>(),
                lookup<com.zhuinden.simplestackdemoexamplefragments.util.MessageQueue>(),
                backstack
            ))
        }
    }

    constructor(parent: FragmentKey) : this(parent, "")

    @Parcelize
    data class AddTaskKey(val parentKey: FragmentKey) : AddOrEditTaskKey(parentKey) {
        override fun getScopeTag(): String = "AddTask"
    }

    @Parcelize
    data class EditTaskKey(val parentKey: FragmentKey, val taskID: String) : AddOrEditTaskKey(parentKey, taskID) {
        override fun getScopeTag(): String = "EditTask[$taskID]"
    }

    override fun layout(): Int = R.layout.path_addoredittask

    override val isFabVisible: Boolean
        get() = true

    override fun instantiateFragment(): Fragment = AddOrEditTaskFragment()

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
