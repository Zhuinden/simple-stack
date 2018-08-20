package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.taskdetail

import android.view.MenuItem
import android.view.View
import butterknife.ButterKnife
import butterknife.Unbinder
import com.zhuinden.simplestackdemoexamplefragments.R
import com.zhuinden.simplestackdemoexamplefragments.application.Injector
import com.zhuinden.simplestackdemoexamplefragments.presentation.objects.Task
import com.zhuinden.simplestackdemoexamplefragments.util.BaseFragment
import com.zhuinden.simplestackdemoexamplefragments.util.Strings
import kotlinx.android.synthetic.main.path_taskdetail.*

/**
 * Created by Owner on 2017. 01. 26..
 */
// UNSCOPED!
class TaskDetailFragment : BaseFragment<TaskDetailFragment, TaskDetailPresenter>() {
    private lateinit var taskDetailPresenter: TaskDetailPresenter

    override fun getPresenter(): TaskDetailPresenter = taskDetailPresenter

    override fun getThis(): TaskDetailFragment = this

    override fun bindViews(view: View): Unbinder {
        return ButterKnife.bind(this, view)
    }

    override fun injectSelf() {
        taskDetailPresenter = Injector.get().taskDetailPresenter()
    }

    fun editTask() {
        taskDetailPresenter.editTask()
    }

    fun showTitle(title: String) {
        textTaskDetailTitle.visibility = View.VISIBLE
        textTaskDetailTitle.text = title
    }

    fun hideTitle() {
        textTaskDetailTitle.visibility = View.GONE
    }

    fun showDescription(description: String) {
        textTaskDetailDescription.visibility = View.VISIBLE
        textTaskDetailDescription.text = description
    }

    fun hideDescription() {
        textTaskDetailDescription.visibility = View.GONE
    }

    fun showMissingTask() {
        textTaskDetailTitle.text = ""
        textTaskDetailDescription.text = activity!!.getString(R.string.no_data)
    }

    fun showTask(task: Task) {
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
