package com.zhuinden.simplestackexamplemvvm.features.statistics


import androidx.fragment.app.Fragment
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackexamplemvvm.application.BaseKey
import com.zhuinden.simplestackexamplemvvm.data.tasks.TasksDataSource
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.lookup
import kotlinx.parcelize.Parcelize

/**
 * Created by Zhuinden on 2017.07.26..
 */
@Parcelize
data class StatisticsKey(private val noArgPlaceHolder: String = "") : BaseKey() {
    override fun instantiateFragment(): Fragment = StatisticsFragment()

    @Suppress("RemoveExplicitTypeArguments")
    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            add(StatisticsViewModel(lookup<TasksDataSource>()))
        }
    }
}