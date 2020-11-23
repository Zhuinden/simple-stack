/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhuinden.simplestackexamplemvvm.features.statistics


import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.zhuinden.livedatacombinetuplekt.combineTuple
import com.zhuinden.simplestackexamplemvvm.R
import com.zhuinden.simplestackexamplemvvm.databinding.StatisticsFragmentBinding
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import com.zhuinden.simplestackextensions.fragmentsktx.lookup

/**
 * Main UI for the statistics screen.
 */
class StatisticsFragment : KeyedFragment(R.layout.statistics_fragment) {
    private val statisticsViewModel by lazy { lookup<StatisticsViewModel>() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = StatisticsFragmentBinding.bind(view)

        combineTuple(statisticsViewModel.activeTasks, statisticsViewModel.completedTasks).observe(viewLifecycleOwner) { (activeTasks, completedTasks) ->
            with(binding) {
                if (activeTasks == null || completedTasks == null) {
                    loadingView.isVisible = true
                    return@with
                }

                loadingView.isVisible = false

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