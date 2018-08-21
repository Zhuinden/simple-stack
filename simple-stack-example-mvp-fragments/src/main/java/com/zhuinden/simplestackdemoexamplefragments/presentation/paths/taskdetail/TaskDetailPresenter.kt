package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.taskdetail

import android.annotation.SuppressLint
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestackdemoexamplefragments.application.Key
import com.zhuinden.simplestackdemoexamplefragments.data.repository.TaskRepository
import com.zhuinden.simplestackdemoexamplefragments.presentation.objects.Task
import com.zhuinden.simplestackdemoexamplefragments.presentation.paths.addoredittask.AddOrEditTaskKey
import com.zhuinden.simplestackdemoexamplefragments.util.BasePresenter
import com.zhuinden.simplestackdemoexamplefragments.util.BaseViewContract
import com.zhuinden.statebundle.StateBundle
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

/**
 * Created by Zhuinden on 2018. 08. 20.
 */

class TaskDetailPresenter @Inject constructor(
    private val taskRepository: TaskRepository,
    private val backstack: Backstack
) : BasePresenter<TaskDetailPresenter.ViewContract>() {
    interface ViewContract: BaseViewContract {
        fun showTask(task: Task)

        fun showMissingTask()
    }

    lateinit var taskDetailKey: TaskDetailKey

    lateinit var taskId: String

    var task: Task? = null

    @SuppressLint("CheckResult")
    override fun onAttach(view: ViewContract) {
        this.taskDetailKey = view.getKey()
        this.taskId = taskDetailKey.taskId()

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

    override fun onDetach(view: ViewContract) {

    }

    fun editTask() {
        if (task == null) {
            view?.showMissingTask()
            return
        }
        backstack.goTo(AddOrEditTaskKey.createWithTaskId(view!!.getKey<Key>(), taskId))
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
