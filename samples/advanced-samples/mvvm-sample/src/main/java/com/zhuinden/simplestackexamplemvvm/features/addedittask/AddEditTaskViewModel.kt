package com.zhuinden.simplestackexamplemvvm.features.addedittask


import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.Bundleable
import com.zhuinden.simplestackexamplemvvm.R
import com.zhuinden.simplestackexamplemvvm.application.SnackbarTextEmitter
import com.zhuinden.simplestackexamplemvvm.data.Task
import com.zhuinden.simplestackexamplemvvm.data.tasks.TasksDataSource
import com.zhuinden.simplestackexamplemvvm.features.tasks.TasksViewModel
import com.zhuinden.statebundle.StateBundle

class AddEditTaskViewModel(
    private val snackbarTextEmitter: SnackbarTextEmitter,
    private val tasksViewModel: TasksViewModel,
    private val tasksDataSource: TasksDataSource,
    private val backstack: Backstack,
    private val key: AddEditTaskKey
) : Bundleable {
    var title: String = key.task?.title ?: ""
    var description: String = key.task?.description ?: ""

    fun onSaveTaskClicked() {
        if (key.task == null) {
            createTask()
        } else {
            updateTask()
        }
    }

    private fun createTask() {
        val newTask: Task = Task.createNewActiveTask(title, description)
        if (newTask.isEmpty) {
            snackbarTextEmitter.emit(R.string.empty_task_message)
        } else {
            tasksDataSource.saveTask(newTask)
            tasksViewModel.onTaskAdded()
            backstack.jumpToRoot()
        }
    }

    private fun updateTask() {
        tasksDataSource.saveTask(Task.createTaskWithId(title, description, key.task?.id, key.task?.completed
            ?: false))
        tasksViewModel.onTaskSaved()
        backstack.jumpToRoot()
    }

    override fun toBundle(): StateBundle {
        val bundle = StateBundle()
        bundle.putString("title", title)
        bundle.putString("description", description)
        return bundle
    }

    override fun fromBundle(bundle: StateBundle?) {
        if (bundle != null) {
            title = bundle.getString("title", "")
            description = bundle.getString("description", "")
        }
    }
}