package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.taskdetail

import android.view.MenuItem
import com.zhuinden.simplestackdemoexamplefragments.R
import com.zhuinden.simplestackdemoexamplefragments.application.Injector
import com.zhuinden.simplestackdemoexamplefragments.presentation.objects.Task
import com.zhuinden.simplestackdemoexamplefragments.util.BaseFragment
import com.zhuinden.simplestackdemoexamplefragments.util.Strings
import com.zhuinden.simplestackdemoexamplefragments.util.hide
import com.zhuinden.simplestackdemoexamplefragments.util.show
import kotlinx.android.synthetic.main.path_taskdetail.*

/**
 * Created by Zhuinden on 2018. 08. 20.
 */
// UNSCOPED!
class TaskDetailFragment : BaseFragment<TaskDetailPresenter.ViewContract, TaskDetailPresenter>(), TaskDetailPresenter.ViewContract {
    private val taskDetailPresenter = Injector.get().taskDetailPresenter()

    override fun getPresenter(): TaskDetailPresenter = taskDetailPresenter

    override fun getThis(): TaskDetailPresenter.ViewContract = this

    fun editTask() {
        taskDetailPresenter.editTask()
    }

    fun showTitle(title: String) {
        textTaskDetailTitle.show()
        textTaskDetailTitle.text = title
    }

    fun hideTitle() {
        textTaskDetailTitle.hide()
    }

    fun showDescription(description: String) {
        textTaskDetailDescription.show()
        textTaskDetailDescription.text = description
    }

    fun hideDescription() {
        textTaskDetailDescription.hide()
    }

    override fun showMissingTask() {
        textTaskDetailTitle.text = ""
        textTaskDetailDescription.text = activity!!.getString(R.string.no_data)
    }

    override fun showTask(task: Task) {
        val title = task.title
        val description = task.description

        if (Strings.isNullOrEmpty(title)) {
            hideTitle()
        } else {
            showTitle(title!!)
        }

        if (Strings.isNullOrEmpty(description)) {
            hideDescription()
        } else {
            showDescription(description!!)
        }
        showCompletionStatus(task, task.isCompleted)
    }

    private fun showCompletionStatus(task: Task, completed: Boolean) {
        checkboxTaskDetailComplete.isChecked = completed
        checkboxTaskDetailComplete.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                presenter.completeTask(task)
            } else {
                presenter.activateTask(task)
            }
        }
    }

    fun deleteTask() {
        taskDetailPresenter.deleteTask()
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.menu_delete -> {
                deleteTask()
                return true
            }
        }
        return false
    }
}
