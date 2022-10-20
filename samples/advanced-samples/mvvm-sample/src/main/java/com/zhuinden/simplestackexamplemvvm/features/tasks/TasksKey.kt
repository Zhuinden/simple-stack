package com.zhuinden.simplestackexamplemvvm.features.tasks


import androidx.fragment.app.Fragment
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackexamplemvvm.application.BaseKey
import com.zhuinden.simplestackexamplemvvm.application.SnackbarTextEmitter
import com.zhuinden.simplestackexamplemvvm.data.tasks.TasksDataSource
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.lookup
import kotlinx.parcelize.Parcelize

@Parcelize
data object TasksKey : BaseKey() {
    override fun instantiateFragment(): Fragment = TasksFragment()

    @Suppress("RemoveExplicitTypeArguments")
    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            add(
                TasksViewModel(
                    lookup<SnackbarTextEmitter>(),
                    lookup<TasksDataSource>(),
                    backstack,
                    getKey()
            ))
        }
    }
}

