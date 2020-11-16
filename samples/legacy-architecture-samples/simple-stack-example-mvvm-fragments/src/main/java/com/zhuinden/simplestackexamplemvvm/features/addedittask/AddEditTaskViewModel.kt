package com.zhuinden.simplestackexamplemvvm.features.addedittask


import android.content.res.Resources
import com.zhuinden.eventemitter.EventEmitter
import com.zhuinden.eventemitter.EventSource
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.Bundleable
import com.zhuinden.simplestackexamplemvvm.R
import com.zhuinden.simplestackexamplemvvm.data.Task
import com.zhuinden.simplestackexamplemvvm.data.source.TasksDataSource
import com.zhuinden.simplestackexamplemvvm.features.tasks.TasksViewModel.AddedTaskMessage
import com.zhuinden.simplestackexamplemvvm.features.tasks.TasksViewModel.SavedTaskMessage
import com.zhuinden.simplestackexamplemvvm.util.MessageQueue
import com.zhuinden.statebundle.StateBundle

class AddEditTaskViewModel(
    private val resources: Resources,
    private val tasksDataSource: TasksDataSource,
    private val messageQueue: MessageQueue,
    private val backstack: Backstack,
    private val key: AddEditTaskKey
) : Bundleable {
    private val snackbarTextEmitter: EventEmitter<String> = EventEmitter()
    val snackbarText: EventSource<String> = snackbarTextEmitter

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
            snackbarTextEmitter.emit(resources.getString(R.string.empty_task_message))
        } else {
            tasksDataSource.saveTask(newTask)
            messageQueue.pushMessageTo(backstack.root(), AddedTaskMessage())
            backstack.jumpToRoot()
        }
    }

    private fun updateTask() {
        tasksDataSource.saveTask(Task.createTaskWithId(title, description, key.task?.id, key.task?.completed
            ?: false))
        messageQueue.pushMessageTo(backstack.root(), SavedTaskMessage())
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