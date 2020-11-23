package com.zhuinden.simplestackexamplemvvm.features.tasks

import android.content.res.Resources
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.zhuinden.eventemitter.EventEmitter
import com.zhuinden.eventemitter.EventSource
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.Bundleable
import com.zhuinden.simplestack.ScopedServices
import com.zhuinden.simplestackexamplemvvm.R
import com.zhuinden.simplestackexamplemvvm.data.Task
import com.zhuinden.simplestackexamplemvvm.data.source.TasksDataSource
import com.zhuinden.simplestackexamplemvvm.features.addedittask.AddEditTaskKey
import com.zhuinden.simplestackexamplemvvm.util.MessageQueue
import com.zhuinden.statebundle.StateBundle

class TasksViewModel(
    private val tasksDataSource: TasksDataSource,
    private val resources: Resources,
    private val backstack: Backstack,
    private val messageQueue: MessageQueue,
    private val key: TasksKey
) : Bundleable, ScopedServices.Activated {
    private val handler = Handler(Looper.getMainLooper()) // todo: inject

    private val isCurrentlyRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = isCurrentlyRefreshing

    val selectedFilter = MutableLiveData(TasksFilterType.ALL_TASKS)

    private val snackBarTextEmitter = EventEmitter<String>()
    val snackbarText: EventSource<String> = snackBarTextEmitter

    val tasks = tasksDataSource.tasksWithChanges

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

        snackBarTextEmitter.emit(resources.getString(R.string.completed_tasks_cleared))
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

    fun refresh() {
        if (isCurrentlyRefreshing.value!!) {
            return
        }

        isCurrentlyRefreshing.value = true
        tasksDataSource.refreshTasks()

        handler.postDelayed({ isCurrentlyRefreshing.value = false }, 2000L)
    }

    class SavedTaskMessage

    class AddedTaskMessage
    class DeletedTaskMessage

    override fun onServiceActive() {
        messageQueue.requestMessages(key) {
            when {
                it is SavedTaskMessage -> {
                    snackBarTextEmitter.emit(resources.getString(R.string.successfully_saved_task_message))
                }
                it is AddedTaskMessage -> {
                    snackBarTextEmitter.emit(resources.getString(R.string.successfully_added_task_message))
                }
                it is DeletedTaskMessage -> {
                    snackBarTextEmitter.emit(resources.getString(R.string.successfully_deleted_task_message))
                }
            }
        }
    }

    override fun onServiceInactive() {
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
