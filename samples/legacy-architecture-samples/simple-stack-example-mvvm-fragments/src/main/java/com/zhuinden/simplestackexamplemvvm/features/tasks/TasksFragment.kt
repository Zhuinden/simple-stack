package com.zhuinden.simplestackexamplemvvm.features.tasks


import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.zhuinden.livedatacombinetuplekt.combineTuple
import com.zhuinden.liveevent.observe
import com.zhuinden.simplestackexamplemvvm.R
import com.zhuinden.simplestackexamplemvvm.data.Task
import com.zhuinden.simplestackexamplemvvm.databinding.TasksFragmentBinding
import com.zhuinden.simplestackexamplemvvm.util.showSnackbar
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import com.zhuinden.simplestackextensions.fragmentsktx.lookup

/**
 * Display a grid of [Task]s. User can choose to view all, active or completed tasks.
 */
class TasksFragment : KeyedFragment(R.layout.tasks_fragment), TasksAdapter.Listener {
    private val tasksViewModel by lazy { lookup<TasksViewModel>() }

    private lateinit var adapter: TasksAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = TasksFragmentBinding.bind(view)
        with(binding) {
            tasksList.adapter = TasksAdapter(this@TasksFragment).also {
                adapter = it
            }

            val swipeRefreshLayout = binding.refreshLayout
            swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(requireActivity(), R.color.colorPrimary),
                ContextCompat.getColor(requireActivity(), R.color.colorAccent),
                ContextCompat.getColor(requireActivity(), R.color.colorPrimaryDark))

            swipeRefreshLayout.setScrollUpChild(tasksList)

            viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    owner.lifecycle.removeObserver(this)

                    swipeRefreshLayout.isRefreshing = false
                    swipeRefreshLayout.destroyDrawingCache()
                    swipeRefreshLayout.clearAnimation()
                }
            })

            noTasksAdd.setOnClickListener {
                tasksViewModel.onAddNewTaskClicked()
            }
        }

        tasksViewModel.snackbarText.observe(viewLifecycleOwner) { snackBarText ->
            showSnackbar(binding.root, snackBarText)
        }

        tasksViewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            binding.noTasksAdd.isVisible = tasks.isEmpty()
        }

        tasksViewModel.filteredTasks.observe(viewLifecycleOwner) { tasks ->
            adapter.replaceData(tasks)
        }

        tasksViewModel.isRefreshing.observe(viewLifecycleOwner) { isRefreshing ->
            binding.refreshLayout.isRefreshing = isRefreshing
        }

        tasksViewModel.selectedFilter.observe(viewLifecycleOwner) { filterType ->
            binding.filteringLabel.text = getString(when (filterType) {
                TasksFilterType.ALL_TASKS -> R.string.label_all
                TasksFilterType.ACTIVE_TASKS -> R.string.label_active
                TasksFilterType.COMPLETED_TASKS -> R.string.label_completed
            })
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_clear -> tasksViewModel.clearCompletedTasks()
            R.id.menu_filter -> showFilteringPopUpMenu()
            R.id.menu_refresh -> tasksViewModel.refresh()
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tasks_fragment_menu, menu)
    }

    private fun showFilteringPopUpMenu() {
        val popup = PopupMenu(requireContext(), requireActivity().findViewById(R.id.menu_filter))
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

    fun onAddNewTaskClicked() {
        tasksViewModel.onAddNewTaskClicked()
    }

    override fun onTaskCheckChanged(task: Task, isChecked: Boolean) {
        tasksViewModel.onTaskCheckChanged(task, isChecked)
    }
}