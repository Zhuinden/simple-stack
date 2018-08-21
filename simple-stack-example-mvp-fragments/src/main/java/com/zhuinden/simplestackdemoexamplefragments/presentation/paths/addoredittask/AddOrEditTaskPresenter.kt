package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.addoredittask

import android.annotation.SuppressLint
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.Bundleable
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestackdemoexamplefragments.data.repository.TaskRepository
import com.zhuinden.simplestackdemoexamplefragments.presentation.objects.Task
import com.zhuinden.simplestackdemoexamplefragments.presentation.paths.tasks.TasksFragment
import com.zhuinden.simplestackdemoexamplefragments.presentation.paths.tasks.TasksKey
import com.zhuinden.simplestackdemoexamplefragments.util.BasePresenter
import com.zhuinden.simplestackdemoexamplefragments.util.BaseViewContract
import com.zhuinden.simplestackdemoexamplefragments.util.MessageQueue
import com.zhuinden.statebundle.StateBundle
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

/**
 * Created by Zhuinden on 2018. 08. 20.
 */
// UNSCOPED
class AddOrEditTaskPresenter @Inject constructor(
    private val taskRepository: TaskRepository,
    private val messageQueue: MessageQueue,
    private val backstack: Backstack
) : BasePresenter<AddOrEditTaskPresenter.ViewContract>(), Bundleable {
    interface ViewContract: BaseViewContract {
        fun setTitle(title: String)

        fun setDescription(description: String)

        fun hideKeyboard()
    }

    var title: String? = null
    var description: String? = null

    var taskId: String? = null

    var task: Task? = null

    fun updateTitle(title: String) {
        this.title = title
    }

    fun updateDescription(description: String) {
        this.description = description
    }

    @SuppressLint("CheckResult")
    override fun onAttach(view: ViewContract) {
        val addOrEditTaskKey: AddOrEditTaskKey = view.getKey()
        taskId = addOrEditTaskKey.taskId

        if (!taskId.isNullOrEmpty()) {
            taskRepository.findTask(addOrEditTaskKey.taskId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { taskOptional ->
                    val task = taskOptional.takeIf { it.isPresent }?.get() ?: return@subscribe
                    this.task = task
                    if (this.title == null || this.description == null) {
                        this.title = task.title
                        this.description = task.description
                        view.setTitle(title!!)
                        view.setDescription(description!!)
                    }
                }
        }
    }

    override fun onDetach(view: ViewContract) {
    }

    override fun toBundle(): StateBundle = StateBundle().apply {
        putString("title", title)
        putString("description", description)
    }

    override fun fromBundle(bundle: StateBundle?) {
        bundle?.run {
            title = getString("title")
            description = getString("description")
        }
    }

    fun saveTask(): Boolean {
        val task = task
        val title = title
        val description = description

        if (title != null && title.isNotEmpty() && description != null && description.isNotEmpty()) {
            taskRepository.insertTask(when {
                taskId.isNullOrEmpty() -> Task.createNewActiveTask(title, description)
                else -> {
                    task?.copy(title = title, description = description) ?: return false
                }
            })
            return true
        }
        return false
    }

    fun navigateBack() {
        view!!.hideKeyboard()
        val addOrEditTaskKey = view!!.getKey<AddOrEditTaskKey>()
        when(addOrEditTaskKey) {
            is AddOrEditTaskKey.AddTaskKey -> {
                messageQueue.pushMessageTo(backstack.root<TasksKey>()!!, TasksFragment.SavedSuccessfullyMessage())
                backstack.goBack()
            }
            is AddOrEditTaskKey.EditTaskKey -> {
                backstack.jumpToRoot(StateChange.BACKWARD)
            }
        }
    }
}
