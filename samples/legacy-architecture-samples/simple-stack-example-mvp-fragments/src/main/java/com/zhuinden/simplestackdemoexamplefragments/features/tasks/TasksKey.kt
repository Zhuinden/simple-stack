package com.zhuinden.simplestackdemoexamplefragments.features.tasks

import android.view.View
import androidx.fragment.app.Fragment
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackdemoexamplefragments.R
import com.zhuinden.simplestackdemoexamplefragments.application.Injector
import com.zhuinden.simplestackdemoexamplefragments.core.navigation.BaseKey
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider
import kotlinx.android.parcel.Parcelize

/**
 * Created by Zhuinden on 2018. 08. 20.
 */

@Parcelize
data class TasksKey(val placeholder: String = "") : BaseKey(), DefaultServiceProvider.HasServices {
    override fun bindServices(serviceBinder: ServiceBinder) {
        serviceBinder.addService(TasksFragment.CONTROLLER_TAG, Injector.get().tasksPresenter())
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
