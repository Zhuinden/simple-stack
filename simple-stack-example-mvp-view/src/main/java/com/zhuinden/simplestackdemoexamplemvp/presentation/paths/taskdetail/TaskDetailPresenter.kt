package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.taskdetail

import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestackdemoexamplemvp.application.Key
import com.zhuinden.simplestackdemoexamplemvp.data.repository.TaskRepository
import com.zhuinden.simplestackdemoexamplemvp.presentation.objects.Task
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.addoredittask.AddOrEditTaskKey
import com.zhuinden.simplestackdemoexamplemvp.util.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

/**
 * Created by Owner on 2017. 01. 27..
 */

class TaskDetailPresenter @Inject constructor(
    private val taskRepository: TaskRepository,
    private val backstack: Backstack
) : BasePresenter<TaskDetailView>() {

    lateinit var taskDetailKey: TaskDetailKey

    var taskId: String = ""

    var task: Task? = null

    override fun onAttach(view: TaskDetailView) {
        taskDetailKey = Backstack.getKey(view.context)
        this.taskId = taskDetailKey.taskId
        taskRepository.findTask(taskId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { taskOptional ->
                val task = taskOptional.takeIf { it.isPresent }?.get()
                this.task = task

                if (task != null) {
                    view.showTask(task)
                } else {
                    view.showMissingTask()
                }
            }
    }

    override fun onDetach(view: TaskDetailView) {
    }

    fun editTask() {
        if (taskId.isEmpty()) {
            view!!.showMissingTask()
            return
        }
        backstack.goTo(AddOrEditTaskKey.EditTaskKey(Backstack.getKey<Key>(view!!.context), taskId))
    }

    fun completeTask(task: Task) {
        taskRepository.setTaskCompleted(task)
    }

    fun activateTask(task: Task) {
        taskRepository.setTaskActive(task)
    }

    fun deleteTask() {
        if (task != null) {
            taskRepository.deleteTask(task!!)
            backstack.goBack()
        }
    }
}
