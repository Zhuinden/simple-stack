package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.statistics

import com.zhuinden.simplestackdemoexamplefragments.R
import com.zhuinden.simplestackdemoexamplefragments.application.Injector
import com.zhuinden.simplestackdemoexamplefragments.util.BaseFragment
import kotlinx.android.synthetic.main.path_statistics.*

/**
 * Created by Zhuinden on 2018. 08. 20.
 */
// UNSCOPED!
class StatisticsFragment : BaseFragment<StatisticsPresenter.ViewContract, StatisticsPresenter>(), StatisticsPresenter.ViewContract {
    private val myResources = Injector.get().resources()
    private val statisticsPresenter = Injector.get().statisticsPresenter()

    override val presenter: StatisticsPresenter = statisticsPresenter
    override fun getThis(): StatisticsFragment = this

    fun setProgressIndicator(active: Boolean) {
        textStatistics.text = when {
            active -> myResources.getString(R.string.loading)
            else -> ""
        }
    }

    override fun showStatistics(numberOfIncompleteTasks: Int, numberOfCompletedTasks: Int) {
        textStatistics.text = when {
            numberOfCompletedTasks == 0 && numberOfIncompleteTasks == 0 -> myResources.getString(R.string.statistics_no_tasks)
            else ->
                "${myResources.getString(R.string.statistics_active_tasks)} $numberOfIncompleteTasks\n" +
                    "${myResources.getString(R.string.statistics_completed_tasks)} $numberOfCompletedTasks"
        }
    }

    fun showLoadingStatisticsError() {
        textStatistics.text = myResources.getString(R.string.statistics_error)
    }
}
