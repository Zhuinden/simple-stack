package com.zhuinden.simplestackdemoexamplefragments.features.taskdetail

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.zhuinden.simplestackdemoexamplefragments.R
import com.zhuinden.simplestackdemoexamplefragments.core.mvp.MvpPresenter
import com.zhuinden.simplestackdemoexamplefragments.core.navigation.BaseFragment
import com.zhuinden.simplestackdemoexamplefragments.data.models.Task
import com.zhuinden.simplestackdemoexamplefragments.util.Strings
import com.zhuinden.simplestackdemoexamplefragments.util.hide
import com.zhuinden.simplestackdemoexamplefragments.util.lookup
import com.zhuinden.simplestackdemoexamplefragments.util.show
import kotlinx.android.synthetic.main.path_taskdetail.*

/**
 * Created by Zhuinden on 2018. 08. 20.
 */
// UNSCOPED!
class TaskDetailFragment : BaseFragment() {
    companion object {
        const val CONTROLLER_TAG = "TaskDetailView.Presenter"
    }

    interface Presenter: MvpPresenter<TaskDetailFragment> {
        fun onTaskChecked(task: Task, checked: Boolean)

        fun onTaskEditButtonClicked()

        fun onTaskDeleteButtonClicked()
    }

    val presenter: Presenter by lazy {
        lookup<Presenter>(CONTROLLER_TAG)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.attachView(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        presenter.detachView(this)
    }

    fun editTask() {
        presenter.onTaskEditButtonClicked()
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
            presenter.onTaskChecked(task, isChecked)
        }
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.menu_delete -> {
                presenter.onTaskDeleteButtonClicked()
                return true
            }
        }
        return false
    }
}
