package com.zhuinden.simplestackdemoexamplefragments.features.taskdetail

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.zhuinden.simplestackdemoexamplefragments.R
import com.zhuinden.simplestackdemoexamplefragments.core.mvp.MvpPresenter
import com.zhuinden.simplestackdemoexamplefragments.core.navigation.BaseFragment
import com.zhuinden.simplestackdemoexamplefragments.data.models.Task
import com.zhuinden.simplestackdemoexamplefragments.databinding.PathTaskdetailBinding
import com.zhuinden.simplestackdemoexamplefragments.util.*

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

    private val binding by viewBinding(PathTaskdetailBinding::bind)

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
        binding.textTaskDetailTitle.show()
        binding.textTaskDetailTitle.text = title
    }

    fun hideTitle() {
        binding.textTaskDetailTitle.hide()
    }

    fun showDescription(description: String) {
        binding.textTaskDetailDescription.show()
        binding.textTaskDetailDescription.text = description
    }

    fun hideDescription() {
        binding.textTaskDetailDescription.hide()
    }

    fun showMissingTask() {
        binding.textTaskDetailTitle.text = ""
        binding.textTaskDetailDescription.text = getString(R.string.no_data)
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
        binding.checkboxTaskDetailComplete.isChecked = completed
        binding.checkboxTaskDetailComplete.setOnCheckedChangeListener { _, isChecked ->
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
