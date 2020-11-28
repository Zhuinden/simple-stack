package com.zhuinden.simplestackdemoexamplefragments.features.statistics

import android.view.View
import androidx.fragment.app.Fragment
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackdemoexamplefragments.R
import com.zhuinden.simplestackdemoexamplefragments.core.navigation.BaseKey
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider
import com.zhuinden.simplestackextensions.servicesktx.lookup
import kotlinx.parcelize.Parcelize

/**
 * Created by Zhuinden on 2018. 08. 20.
 */
@Parcelize
data class StatisticsKey(val placeholder: String = "") : BaseKey(), DefaultServiceProvider.HasServices {
    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            addService(StatisticsFragment.CONTROLLER_TAG, StatisticsPresenter(
                lookup()
            ))
        }
    }

    override fun getScopeTag(): String = "Statistics"

    constructor() : this("")

    override fun layout(): Int = R.layout.path_statistics

    override val isFabVisible: Boolean
        get() = false

    override fun instantiateFragment(): Fragment = StatisticsFragment()

    override fun menu(): Int = R.menu.empty_menu

    override fun navigationViewId(): Int = R.id.statistics_navigation_menu_item

    override fun shouldShowUp(): Boolean = false

    override fun fabClickListener(fragment: Fragment): View.OnClickListener =
        View.OnClickListener { v ->

        }

    override fun fabDrawableIcon(): Int = 0
}
