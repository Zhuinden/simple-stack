package com.zhuinden.simplestackexamplemvvm.features.statistics

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.zhuinden.livedatacombinetuplekt.combineTuple
import com.zhuinden.simplestackexamplemvvm.R
import com.zhuinden.simplestackexamplemvvm.databinding.StatisticsFragmentBinding
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import com.zhuinden.simplestackextensions.fragmentsktx.lookup

class StatisticsFragment : KeyedFragment(R.layout.statistics_fragment) {
    private val statisticsViewModel by lazy { lookup<StatisticsViewModel>() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = StatisticsFragmentBinding.bind(view)

        binding.toolbar.textTitle.text = getString(R.string.statistics_title)

        @Suppress("NAME_SHADOWING")
        combineTuple(statisticsViewModel.activeTasks, statisticsViewModel.completedTasks).observe(viewLifecycleOwner) { (activeTasks, completedTasks) ->
            with(binding) {
                val activeTasks = activeTasks ?: return@observe
                val completedTasks = completedTasks ?: return@observe

                val isNoTasks = activeTasks.size + completedTasks.size == 0
                statisticsNoTasks.isVisible = isNoTasks
                this.statisticsActiveTasks.text = String.format(getString(R.string.statistics_active_tasks), activeTasks.size)
                this.statisticsActiveTasks.isVisible = !isNoTasks
                this.statisticsCompletedTasks.text = String.format(getString(R.string.statistics_completed_tasks), completedTasks.size)
                this.statisticsCompletedTasks.isVisible = !isNoTasks
            }
        }
    }
}