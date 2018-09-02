package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.statistics

import com.zhuinden.simplestackdemoexamplefragments.R
import com.zhuinden.simplestackdemoexamplefragments.application.Injector
import com.zhuinden.simplestackdemoexamplefragments.util.BaseFragment
import com.zhuinden.simplestackdemoexamplefragments.util.MvpPresenter
import com.zhuinden.simplestackdemoexamplefragments.util.backstackDelegate
import kotlinx.android.synthetic.main.path_statistics.*

/**
 * Created by Zhuinden on 2018. 08. 20.
 */
// UNSCOPED!
class StatisticsFragment : BaseFragment<StatisticsFragment, StatisticsFragment.Presenter>() {
    private val myResources = Injector.get().resources()

    companion object {
        const val CONTROLLER_TAG = "AddOrEditTaskPresenter"
    }

    interface Presenter: MvpPresenter<StatisticsFragment> {
    }

    override val presenter: Presenter by lazy {
        backstackDelegate.lookupService<StatisticsFragment.Presenter>(CONTROLLER_TAG)
    }

    override fun getThis(): StatisticsFragment = this

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
