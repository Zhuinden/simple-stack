package com.zhuinden.simplestackdemoexamplefragments.features.statistics

import android.os.Bundle
import android.view.View
import com.zhuinden.simplestackdemoexamplefragments.R
import com.zhuinden.simplestackdemoexamplefragments.core.mvp.MvpPresenter
import com.zhuinden.simplestackdemoexamplefragments.core.navigation.BaseFragment
import com.zhuinden.simplestackdemoexamplefragments.databinding.PathStatisticsBinding
import com.zhuinden.simplestackdemoexamplefragments.util.lookup
import com.zhuinden.simplestackdemoexamplefragments.util.viewBinding

/**
 * Created by Zhuinden on 2018. 08. 20.
 */
// UNSCOPED!
class StatisticsFragment : BaseFragment() {
    companion object {
        const val CONTROLLER_TAG = "AddOrEditTaskPresenter"
    }

    interface Presenter : MvpPresenter<StatisticsFragment> {
    }

    val presenter: Presenter by lazy {
        lookup<Presenter>(CONTROLLER_TAG)
    }

    private val binding by viewBinding(PathStatisticsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.attachView(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        presenter.detachView(this)
    }

    fun setProgressIndicator(active: Boolean) {
        binding.textStatistics.text = when {
            active -> getString(R.string.loading)
            else -> ""
        }
    }

    fun showStatistics(numberOfIncompleteTasks: Int, numberOfCompletedTasks: Int) {
        binding.textStatistics.text = when {
            numberOfCompletedTasks == 0 && numberOfIncompleteTasks == 0 -> getString(R.string.statistics_no_tasks)
            else ->
                "${getString(R.string.statistics_active_tasks)} $numberOfIncompleteTasks\n" +
                    "${getString(R.string.statistics_completed_tasks)} $numberOfCompletedTasks"
        }
    }

    fun showLoadingStatisticsError() {
        binding.textStatistics.text = getString(R.string.statistics_error)
    }
}
