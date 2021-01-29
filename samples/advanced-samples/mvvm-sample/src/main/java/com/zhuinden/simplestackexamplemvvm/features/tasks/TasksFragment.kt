package com.zhuinden.simplestackexamplemvvm.features.tasks


import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupieAdapter
import com.zhuinden.livedatacombinetuplekt.combineTuple
import com.zhuinden.simplestackexamplemvvm.R
import com.zhuinden.simplestackexamplemvvm.data.Task
import com.zhuinden.simplestackexamplemvvm.databinding.TasksFragmentBinding
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import com.zhuinden.simplestackextensions.fragmentsktx.lookup

class TasksFragment : KeyedFragment(R.layout.tasks_fragment), TaskItem.Listener {
    private val tasksViewModel by lazy { lookup<TasksViewModel>() }

    private val adapter = GroupieAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = TasksFragmentBinding.bind(view)
        with(binding) {
            tasksList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            tasksList.adapter = adapter

            noTasksAdd.setOnClickListener {
                tasksViewModel.onAddNewTaskClicked()
            }

            fab.setOnClickListener {
                tasksViewModel.onAddNewTaskClicked()
            }
        }

        tasksViewModel.hasTasks.observe(viewLifecycleOwner) { hasTasks ->
            binding.noTasksAdd.isVisible = !hasTasks
        }

        tasksViewModel.filteredTasks.observe(viewLifecycleOwner) { tasks ->
            adapter.replaceAll(tasks.map { TaskItem(it, this) })
        }

        tasksViewModel.selectedFilter.observe(viewLifecycleOwner) { filterType ->
            binding.filteringLabel.text = getString(when (filterType) {
                TasksFilterType.ALL_TASKS -> R.string.label_all
                TasksFilterType.ACTIVE_TASKS -> R.string.label_active
                TasksFilterType.COMPLETED_TASKS -> R.string.label_completed
            })
        }

        binding.toolbar.root.addExtraAction("Clear Completed", R.drawable.ic_baseline_delete_sweep_white_24) {
            tasksViewModel.clearCompletedTasks()
        }

        binding.toolbar.root.addExtraAction("Filter", R.drawable.ic_filter_list) { anchor ->
            showFilteringPopUpMenu(anchor)
        }

        @Suppress("NAME_SHADOWING")
        combineTuple(tasksViewModel.selectedFilter, tasksViewModel.filteredTasks)
            .observe(viewLifecycleOwner) { (selectedFilter, filteredTasks) ->
                val selectedFilter = selectedFilter ?: return@observe
                val filteredTasks = filteredTasks ?: return@observe

                binding.tasksLL.isVisible = filteredTasks.isNotEmpty()
                binding.noTasks.isVisible = filteredTasks.isEmpty()
                binding.noTasksIcon.setImageResource(when (selectedFilter) {
                    TasksFilterType.ALL_TASKS -> R.drawable.ic_assignment_turned_in_24dp
                    TasksFilterType.ACTIVE_TASKS -> R.drawable.ic_check_circle_24dp
                    TasksFilterType.COMPLETED_TASKS -> R.drawable.ic_verified_user_24dp
                })
                binding.noTasksMain.setText(when (selectedFilter) {
                    TasksFilterType.ALL_TASKS -> R.string.no_tasks_all
                    TasksFilterType.ACTIVE_TASKS -> R.string.no_tasks_active
                    TasksFilterType.COMPLETED_TASKS -> R.string.no_tasks_completed
                })
            }
    }

    private fun showFilteringPopUpMenu(anchor: View) {
        val popup = PopupMenu(requireContext(), anchor)
        popup.menuInflater.inflate(R.menu.filter_tasks, popup.menu)
        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.active -> tasksViewModel.setFiltering(TasksFilterType.ACTIVE_TASKS)
                R.id.completed -> tasksViewModel.setFiltering(TasksFilterType.COMPLETED_TASKS)
                else -> tasksViewModel.setFiltering(TasksFilterType.ALL_TASKS)
            }

            true
        }
        popup.show()
    }

    override fun onTaskCheckChanged(task: Task, isChecked: Boolean) {
        tasksViewModel.onTaskCheckChanged(task, isChecked)
    }
}