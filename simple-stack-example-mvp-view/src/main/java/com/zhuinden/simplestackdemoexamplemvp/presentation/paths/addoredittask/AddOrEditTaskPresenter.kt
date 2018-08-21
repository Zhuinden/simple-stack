package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.addoredittask

import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.Bundleable
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestackdemoexamplemvp.data.repository.TaskRepository
import com.zhuinden.simplestackdemoexamplemvp.presentation.objects.Task
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.tasks.TasksKey
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.tasks.TasksView
import com.zhuinden.simplestackdemoexamplemvp.util.BasePresenter
import com.zhuinden.simplestackdemoexamplemvp.util.MessageQueue
import com.zhuinden.simplestackdemoexamplemvp.util.hideKeyboard
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
) : BasePresenter<AddOrEditTaskView>(), Bundleable {
    private var title: String? = null
    private var description: String? = null

    private var taskId: String = ""

    private var task: Task? = null

    fun updateTitle(title: String) {
        this.title = title
    }

    fun updateDescription(description: String) {
        this.description = description
    }

    override fun onAttach(view: AddOrEditTaskView) {
        val addOrEditTaskKey = Backstack.getKey<AddOrEditTaskKey>(view.context)
        taskId = addOrEditTaskKey.taskId

        if (taskId.isNotEmpty()) {
            taskRepository.findTask(addOrEditTaskKey.taskId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { taskOptional ->
                    val task = taskOptional.takeIf { it.isPresent }?.get() ?: return@subscribe
                    this.task = task

                    if (this.title == null || this.description == null) {
                        this.title = task.title
                        this.description = task.description
                        view.setTitle(title ?: "")
                        view.setDescription(description ?: "")
                    }
                }
        }
    }

    override fun onDetach(view: AddOrEditTaskView) {

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
        val title = title
        val description = description
        val task = task

        if (title.isNullOrEmpty() || description.isNullOrEmpty()) {
            return false
        }

        taskRepository.insertTask(when {
            taskId.isEmpty() -> {
                Task.createNewActiveTask(title!!, description!!)
            }
            else -> {
                task?.copy(title = title, description = description) ?: return false
            }
        })
        return true

    }

    fun navigateBack() {
        val addOrEditTaskKey = Backstack.getKey<AddOrEditTaskKey>(view!!.context)
        view!!.hideKeyboard()
        when(addOrEditTaskKey) {
            is AddOrEditTaskKey.AddTaskKey -> {
                messageQueue.pushMessageTo(backstack.root<TasksKey>()!!, TasksView.SavedSuccessfullyMessage())
                backstack.goBack()
            }
            is AddOrEditTaskKey.EditTaskKey -> {
                backstack.jumpToRoot(StateChange.BACKWARD)
            }
        }
    }
}
