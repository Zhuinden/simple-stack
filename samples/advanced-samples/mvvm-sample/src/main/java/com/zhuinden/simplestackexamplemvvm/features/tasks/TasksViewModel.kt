package com.zhuinden.simplestackexamplemvvm.features.tasks

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.Bundleable
import com.zhuinden.simplestackexamplemvvm.R
import com.zhuinden.simplestackexamplemvvm.application.SnackbarTextEmitter
import com.zhuinden.simplestackexamplemvvm.data.Task
import com.zhuinden.simplestackexamplemvvm.data.tasks.TasksDataSource
import com.zhuinden.simplestackexamplemvvm.features.addedittask.AddEditTaskKey
import com.zhuinden.statebundle.StateBundle

class TasksViewModel(
    private val snackbarTextEmitter: SnackbarTextEmitter,
    private val tasksDataSource: TasksDataSource,
    private val backstack: Backstack,
    private val key: TasksKey
) : Bundleable {
    val selectedFilter = MutableLiveData(TasksFilterType.ALL_TASKS)

    val hasTasks = tasksDataSource.tasksWithChanges.map { it.isNotEmpty() }

    @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
    val filteredTasks = selectedFilter.switchMap { filterType ->
        when (filterType) {
            TasksFilterType.ALL_TASKS -> tasksDataSource.tasksWithChanges
            TasksFilterType.ACTIVE_TASKS -> tasksDataSource.activeTasksWithChanges
            TasksFilterType.COMPLETED_TASKS -> tasksDataSource.completedTasksWithChanges
        }
    }

    fun setFiltering(requestType: TasksFilterType) {
        selectedFilter.value = requestType
    }

    fun clearCompletedTasks() {
        tasksDataSource.clearCompletedTasks()

        snackbarTextEmitter.emit(R.string.completed_tasks_cleared)
    }

    fun onAddNewTaskClicked() {
        backstack.goTo(AddEditTaskKey(null))
    }

    fun onTaskCheckChanged(task: Task, checked: Boolean) {
        if (checked) {
            tasksDataSource.completeTask(task)
        } else {
            tasksDataSource.activateTask(task)
        }
    }

    fun onTaskSaved() {
        snackbarTextEmitter.emit(R.string.successfully_saved_task_message)
    }

    fun onTaskAdded() {
        snackbarTextEmitter.emit(R.string.successfully_added_task_message)
    }

    fun onTaskDeleted() {
        snackbarTextEmitter.emit(R.string.successfully_deleted_task_message)
    }

    override fun toBundle(): StateBundle {
        val stateBundle = StateBundle()
        stateBundle.putString("filterType", selectedFilter.value!!.name)
        return stateBundle
    }

    override fun fromBundle(bundle: StateBundle?) {
        if (bundle != null) {
            setFiltering(TasksFilterType.valueOf(bundle.getString("filterType")!!))
        }
    }
}
