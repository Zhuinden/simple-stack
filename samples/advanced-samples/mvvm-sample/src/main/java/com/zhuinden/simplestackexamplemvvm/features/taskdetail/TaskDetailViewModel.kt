package com.zhuinden.simplestackexamplemvvm.features.taskdetail


import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestackexamplemvvm.data.Task
import com.zhuinden.simplestackexamplemvvm.data.tasks.TasksDataSource
import com.zhuinden.simplestackexamplemvvm.features.addedittask.AddEditTaskKey
import com.zhuinden.simplestackexamplemvvm.features.tasks.TasksViewModel

class TaskDetailViewModel(
    private val tasksViewModel: TasksViewModel,
    private val tasksDataSource: TasksDataSource,
    private val backstack: Backstack,
    private val task: Task
) {
    fun onDeleteTaskClicked() {
        tasksDataSource.deleteTask(task.id!!)
        tasksViewModel.onTaskDeleted()
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