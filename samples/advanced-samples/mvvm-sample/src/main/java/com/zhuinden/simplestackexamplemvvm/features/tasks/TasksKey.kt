package com.zhuinden.simplestackexamplemvvm.features.tasks


import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zhuinden.simplestack.ServiceBinder

import com.zhuinden.simplestackexamplemvvm.R
import com.zhuinden.simplestackexamplemvvm.application.BaseKey
import com.zhuinden.simplestackexamplemvvm.application.injection.ApplicationComponent
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.lookup
import kotlinx.parcelize.Parcelize

/**
 * Created by Zhuinden on 2017.07.26..
 */
@Parcelize
data class TasksKey(private val noArgsPlaceHolder: String = "") : BaseKey() {
    override fun instantiateFragment(): Fragment = TasksFragment()

    override fun bindServices(serviceBinder: ServiceBinder) {
        val component = serviceBinder.lookup<ApplicationComponent>()

        with(serviceBinder) {
            add(TasksViewModel(
                component.tasksDataSource(),
                component.resources(),
                backstack,
                component.messageQueue(),
                getKey()
            ))
        }
    }

    override fun navigationViewId(): Int {
        return R.id.list_navigation_menu_item
    }

    override fun shouldShowUp(): Boolean {
        return false
    }

    override fun setupFab(fragment: Fragment, fab: FloatingActionButton) {
        fab.setImageResource(R.drawable.ic_add)
        fab.setOnClickListener { v: View? -> (fragment as TasksFragment).onAddNewTaskClicked() }
    }

    override val isFabVisible: Boolean
        get() = true
}

