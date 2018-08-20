package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.taskdetail

import android.annotation.SuppressLint
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestackdemoexamplefragments.application.Key
import com.zhuinden.simplestackdemoexamplefragments.data.repository.TaskRepository
import com.zhuinden.simplestackdemoexamplefragments.presentation.objects.Task
import com.zhuinden.simplestackdemoexamplefragments.presentation.paths.addoredittask.AddOrEditTaskKey
import com.zhuinden.simplestackdemoexamplefragments.util.BasePresenter
import com.zhuinden.statebundle.StateBundle
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

/**
 * Created by Owner on 2017. 01. 27..
 */

class TaskDetailPresenter @Inject constructor(
    private val taskRepository: TaskRepository,
    private val backstack: Backstack
) : BasePresenter<TaskDetailFragment, TaskDetailPresenter>() {

    lateinit var taskDetailKey: TaskDetailKey

    lateinit var taskId: String

    var task: Task? = null

    @SuppressLint("CheckResult")
    override fun onAttach(fragment: TaskDetailFragment) {
        this.taskDetailKey = fragment.getKey()
        this.taskId = taskDetailKey.taskId()

        taskRepository.findTask(taskId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { taskOptional ->
                val task = taskOptional.takeIf { it.isPresent }?.get()
                this.task = task
                if(task != null) {
                    fragment.showTask(task)
                } else {
                    fragment.showMissingTask()
                }
            }
    }

    override fun onDetach(fragment: TaskDetailFragment) {

    }

    fun editTask() {
        if (task == null) {
            fragment?.showMissingTask()
            return
        }
        backstack.goTo(AddOrEditTaskKey.createWithTaskId(fragment!!.getKey<Key>(), taskId))
    }

    fun completeTask(task: Task) {
        taskRepository.setTaskCompleted(task)
    }

    fun activateTask(task: Task) {
        taskRepository.setTaskActive(task)
    }

    fun deleteTask() {
        val task = task
        if (task != null) {
            taskRepository.deleteTask(task)
            backstack.goBack()
        }
    }

    override fun toBundle(): StateBundle = StateBundle()

    override fun fromBundle(bundle: StateBundle?) {}
}
