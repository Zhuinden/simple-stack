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
import com.zhuinden.simplestackdemoexamplefragments.util.MessageQueue
import com.zhuinden.statebundle.StateBundle
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

/**
 * Created by Owner on 2017. 01. 27..
 */
// UNSCOPED
class AddOrEditTaskPresenter @Inject constructor(
    private val taskRepository: TaskRepository,
    private val messageQueue: MessageQueue,
    private val backstack: Backstack
) : BasePresenter<AddOrEditTaskFragment, AddOrEditTaskPresenter>(), Bundleable {
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
    override fun onAttach(fragment: AddOrEditTaskFragment) {
        val addOrEditTaskKey = fragment.getKey<AddOrEditTaskKey>()

        taskId = addOrEditTaskKey.taskId()

        if (!taskId.isNullOrEmpty()) {
            taskRepository.findTask(addOrEditTaskKey.taskId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { taskOptional ->
                    val task = taskOptional.takeIf { it.isPresent }?.get() ?: return@subscribe
                    this.task = task
                    if (this.title == null || this.description == null) {
                        this.title = task.title
                        this.description = task.description
                        fragment.setTitle(title!!)
                        fragment.setDescription(description!!)
                    }
                }
        }
    }

    override fun onDetach(fragment: AddOrEditTaskFragment) {
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

        if (!title.isNullOrEmpty() && !description.isNullOrEmpty()) {
            taskRepository.insertTask(when {
                taskId.isNullOrEmpty() -> Task.createNewActiveTask(title!!, description!!)
                else -> {
                    task?.copy(title = title, description = description) ?: return false
                }
            })
            return true
        }
        return false
    }

    fun navigateBack() {
        val addOrEditTaskKey = fragment!!.getKey<AddOrEditTaskKey>()
        if (addOrEditTaskKey.parent() is TasksKey) {
            messageQueue.pushMessageTo(addOrEditTaskKey.parent(), TasksFragment.SavedSuccessfullyMessage())
            backstack.goBack()
        } else {
            backstack.jumpToRoot(StateChange.BACKWARD)
        }
    }
}
