package com.zhuinden.simplestackexamplemvvm.features.statistics


import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zhuinden.simplestack.ServiceBinder

import com.zhuinden.simplestackexamplemvvm.R
import com.zhuinden.simplestackexamplemvvm.application.BaseKey
import com.zhuinden.simplestackexamplemvvm.application.injection.Injector
import com.zhuinden.simplestackextensions.servicesktx.add
import kotlinx.android.parcel.Parcelize

/**
 * Created by Zhuinden on 2017.07.26..
 */
@Parcelize
data class StatisticsKey(private val noArgPlaceHolder: String = "") : BaseKey() {
    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            add(StatisticsViewModel(Injector.get().tasksDataSource()))
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