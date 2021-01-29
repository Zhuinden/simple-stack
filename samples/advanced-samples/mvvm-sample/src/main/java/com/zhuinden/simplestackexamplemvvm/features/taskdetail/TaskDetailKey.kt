package com.zhuinden.simplestackexamplemvvm.features.taskdetail

import androidx.fragment.app.Fragment
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackexamplemvvm.application.BaseKey
import com.zhuinden.simplestackexamplemvvm.data.Task
import com.zhuinden.simplestackexamplemvvm.data.tasks.TasksDataSource
import com.zhuinden.simplestackexamplemvvm.features.tasks.TasksViewModel
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.lookup
import kotlinx.parcelize.Parcelize

@Parcelize
data class TaskDetailKey(val task: Task) : BaseKey() {
    override fun instantiateFragment(): Fragment = TaskDetailFragment()

    @Suppress("RemoveExplicitTypeArguments")
    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            add(TaskDetailViewModel(
                lookup<TasksViewModel>(),
                lookup<TasksDataSource>(),
                backstack,
                getKey<TaskDetailKey>().task
            ))
        }
    }
}