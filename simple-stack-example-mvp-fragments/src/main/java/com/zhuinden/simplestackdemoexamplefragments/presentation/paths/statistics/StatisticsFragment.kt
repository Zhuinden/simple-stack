package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.statistics

import android.content.res.Resources
import android.view.View
import butterknife.ButterKnife
import butterknife.Unbinder
import com.zhuinden.simplestackdemoexamplefragments.R
import com.zhuinden.simplestackdemoexamplefragments.application.Injector
import com.zhuinden.simplestackdemoexamplefragments.util.BaseFragment
import kotlinx.android.synthetic.main.path_statistics.*

/**
 * Created by Owner on 2017. 01. 26..
 */
// UNSCOPED!
class StatisticsFragment : BaseFragment<StatisticsFragment, StatisticsPresenter>() {
    private lateinit var myResources: Resources
    private lateinit var statisticsPresenter: StatisticsPresenter

    override fun getPresenter(): StatisticsPresenter {
        return statisticsPresenter
    }

    override fun getThis(): StatisticsFragment {
        return this
    }

    override fun bindViews(view: View): Unbinder {
        return ButterKnife.bind(this, view)
    }

    override fun injectSelf() {
        myResources = Injector.get().resources()
        statisticsPresenter = Injector.get().statisticsPresenter()
    }

    fun setProgressIndicator(active: Boolean) {
        textStatistics.text = when {
            active -> myResources.getString(R.string.loading)
            else -> ""
        }
    }

    fun showStatistics(numberOfIncompleteTasks: Int, numberOfCompletedTasks: Int) {
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
