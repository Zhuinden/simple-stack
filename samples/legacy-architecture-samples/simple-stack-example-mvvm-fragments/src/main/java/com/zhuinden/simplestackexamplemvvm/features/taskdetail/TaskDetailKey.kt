package com.zhuinden.simplestackexamplemvvm.features.taskdetail


import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zhuinden.simplestack.ServiceBinder

import com.zhuinden.simplestackexamplemvvm.R
import com.zhuinden.simplestackexamplemvvm.application.BaseKey
import com.zhuinden.simplestackexamplemvvm.application.injection.Injector
import com.zhuinden.simplestackexamplemvvm.data.Task
import com.zhuinden.simplestackextensions.servicesktx.add
import kotlinx.android.parcel.Parcelize

/**
 * Created by Zhuinden on 2017.07.26..
 */
@Parcelize
data class TaskDetailKey(val task: Task) : BaseKey() {
    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            add(TaskDetailViewModel(
                Injector.get().tasksDataSource(),
                backstack,
                Injector.get().messageQueue(),
                getKey<TaskDetailKey>().task
            ))
        }
    }

    override val isFabVisible: Boolean
        get() = true

    override fun setupFab(fragment: Fragment, fab: FloatingActionButton) {
        fab.setImageResource(R.drawable.ic_edit)
        fab.setOnClickListener { v: View? -> (fragment as TaskDetailFragment).onEditTaskClicked() }
    }

    override fun navigationViewId(): Int {
        return 0
    }

    override fun shouldShowUp(): Boolean {
        return true
    }

    override fun instantiateFragment(): Fragment = TaskDetailFragment()
}