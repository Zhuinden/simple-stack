package com.zhuinden.simplestackexamplemvvm.features.addedittask


import androidx.fragment.app.Fragment
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackexamplemvvm.application.BaseKey
import com.zhuinden.simplestackexamplemvvm.data.Task
import com.zhuinden.simplestackexamplemvvm.data.tasks.TasksDataSource
import com.zhuinden.simplestackexamplemvvm.features.tasks.TasksViewModel
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.lookup
import kotlinx.parcelize.Parcelize

/**
 * Created by Zhuinden on 2017.07.26..
 */
@Parcelize
data class AddEditTaskKey(val task: Task?) : BaseKey() {
    override fun instantiateFragment(): Fragment = AddEditTaskFragment()

    @Suppress("RemoveExplicitTypeArguments")
    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            add(AddEditTaskViewModel(
                lookup<TasksViewModel>(),
                lookup<TasksDataSource>(),
                backstack,
                getKey(),
            ))
        }
    }
}