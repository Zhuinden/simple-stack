package com.zhuinden.simplestackexamplemvvm.features.statistics


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
data class StatisticsKey(private val noArgPlaceHolder: String = "") : BaseKey() {
    override fun bindServices(serviceBinder: ServiceBinder) {
        val component = serviceBinder.lookup<ApplicationComponent>()

        with(serviceBinder) {
            add(StatisticsViewModel(component.tasksDataSource()))
        }
    }

    override val isFabVisible: Boolean
        get() = false

    override fun shouldShowUp(): Boolean = false

    override fun setupFab(fragment: Fragment, fab: FloatingActionButton) {
        // do nothing
    }

    override fun navigationViewId(): Int {
        return R.id.statistics_navigation_menu_item
    }

    override fun instantiateFragment(): Fragment = StatisticsFragment()
}