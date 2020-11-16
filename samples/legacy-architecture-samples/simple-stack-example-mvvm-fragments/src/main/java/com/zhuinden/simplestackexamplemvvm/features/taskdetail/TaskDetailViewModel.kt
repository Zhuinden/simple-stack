package com.zhuinden.simplestackexamplemvvm.features.taskdetail


import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestackexamplemvvm.data.Task
import com.zhuinden.simplestackexamplemvvm.data.source.TasksDataSource
import com.zhuinden.simplestackexamplemvvm.features.addedittask.AddEditTaskKey
import com.zhuinden.simplestackexamplemvvm.features.tasks.TasksViewModel.DeletedTaskMessage
import com.zhuinden.simplestackexamplemvvm.util.MessageQueue

class TaskDetailViewModel(
    private val tasksDataSource: TasksDataSource,
    private val backstack: Backstack,
    private val messageQueue: MessageQueue,
    private val task: Task
) {
    fun onDeleteTaskClicked() {
        tasksDataSource.deleteTask(task.id!!)
        messageQueue.pushMessageTo(backstack.root(), DeletedTaskMessage())
        backstack.jumpToRoot()
    }

    fun onEditTaskClicked() {
        backstack.goTo(AddEditTaskKey(task))
    }

    fun onTaskCheckChanged(checked: Boolean) {
        if (checked) {
            tasksDataSource.completeTask(task)
        } else {
            tasksDataSource.activateTask(task)
        }
    }
}