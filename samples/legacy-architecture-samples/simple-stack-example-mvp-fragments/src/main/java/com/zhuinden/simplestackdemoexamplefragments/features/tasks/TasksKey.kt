package com.zhuinden.simplestackdemoexamplefragments.features.tasks

import android.view.View
import androidx.fragment.app.Fragment
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackdemoexamplefragments.R
import com.zhuinden.simplestackdemoexamplefragments.core.navigation.BaseKey
import com.zhuinden.simplestackdemoexamplefragments.data.repository.TaskRepository
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider
import com.zhuinden.simplestackextensions.servicesktx.lookup
import kotlinx.parcelize.Parcelize

/**
 * Created by Zhuinden on 2018. 08. 20.
 */

@Parcelize
data class TasksKey(val placeholder: String = "") : BaseKey(), DefaultServiceProvider.HasServices {
    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            addService(TasksFragment.CONTROLLER_TAG, TasksPresenter(
                backstack,
                lookup<TaskRepository>()
            ))
        }
    }

    override fun getScopeTag(): String = "Tasks"

    constructor() : this("")

    override fun layout(): Int = R.layout.path_tasks

    override val isFabVisible: Boolean
        get() = true

    override fun instantiateFragment(): Fragment = TasksFragment()

    override fun menu(): Int = R.menu.tasks_fragment_menu

    override fun navigationViewId(): Int = R.id.list_navigation_menu_item

    override fun shouldShowUp(): Boolean = false

    override fun fabClickListener(fragment: Fragment): View.OnClickListener =
        View.OnClickListener { v ->
            @Suppress("NAME_SHADOWING")
            val fragment = fragment as TasksFragment
            fragment.addTaskButtonClicked()
        }

    override fun fabDrawableIcon(): Int = R.drawable.ic_add
}
