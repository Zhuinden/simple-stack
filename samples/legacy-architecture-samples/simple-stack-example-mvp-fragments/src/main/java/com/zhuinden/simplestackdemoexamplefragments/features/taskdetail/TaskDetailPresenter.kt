package com.zhuinden.simplestackdemoexamplefragments.features.taskdetail

import android.annotation.SuppressLint
import com.zhuinden.simplestackdemoexamplefragments.core.mvp.BasePresenter
import com.zhuinden.simplestackdemoexamplefragments.data.models.Task
import com.zhuinden.simplestackdemoexamplefragments.data.repository.TaskRepository
import com.zhuinden.simplestackdemoexamplefragments.features.addoredittask.AddOrEditTaskKey
import com.zhuinden.simplestackdemoexamplefragments.util.BackstackHolder
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

/**
 * Created by Zhuinden on 2018. 08. 20.
 */

class TaskDetailPresenter @Inject constructor(
    private val taskRepository: TaskRepository,
    private val backstackHolder: BackstackHolder
) : BasePresenter<TaskDetailFragment>(), TaskDetailFragment.Presenter {
    override fun onTaskChecked(task: Task, checked: Boolean) {
        if (checked) {
            completeTask(task)
        } else {
            activateTask(task)
        }
    }

    override fun onTaskEditButtonClicked() {
        editTask()
    }

    override fun onTaskDeleteButtonClicked() {
        deleteTask()
    }

    lateinit var taskDetailKey: TaskDetailKey

    lateinit var taskId: String

    var task: Task? = null

    @SuppressLint("CheckResult")
    override fun onAttach(view: TaskDetailFragment) {
        this.taskDetailKey = view.getKey()
        this.taskId = taskDetailKey.taskId

        taskRepository.findTask(taskId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { taskOptional ->
                val task = taskOptional.takeIf { it.isPresent }?.get()
                this.task = task
                if(task != null) {
                    view.showTask(task)
                } else {
                    view.showMissingTask()
                }
            }
    }

    override fun onDetach(view: TaskDetailFragment) {

    }

    private fun editTask() {
        if (task == null) {
            view?.showMissingTask()
            return
        }
        backstackHolder.backstack.goTo(AddOrEditTaskKey.EditTaskKey(view!!.getKey(), taskId))
    }

    private fun completeTask(task: Task) {
        taskRepository.setTaskCompleted(task)
    }

    private fun activateTask(task: Task) {
        taskRepository.setTaskActive(task)
    }

    private fun deleteTask() {
        val task = task
        if (task != null) {
            taskRepository.deleteTask(task)
            backstackHolder.backstack.goBack()
        }
    }
}
