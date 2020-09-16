package com.zhuinden.simplestackdemoexamplemvp.features.taskdetail

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.view.MenuItem
import android.widget.RelativeLayout
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestackdemoexamplemvp.R
import com.zhuinden.simplestackdemoexamplemvp.application.MainActivity
import com.zhuinden.simplestackdemoexamplemvp.core.mvp.MvpPresenter
import com.zhuinden.simplestackdemoexamplemvp.data.models.Task
import com.zhuinden.simplestackdemoexamplemvp.util.Strings
import com.zhuinden.simplestackdemoexamplemvp.util.hide
import com.zhuinden.simplestackdemoexamplemvp.util.show
import kotlinx.android.synthetic.main.path_taskdetail.view.*

/**
 * Created by Zhuinden on 2017.01.26..
 */

class TaskDetailView : RelativeLayout, MainActivity.OptionsItemSelectedListener {
    companion object {
        const val CONTROLLER_TAG = "TaskDetailView.Presenter"
    }

    interface Presenter: MvpPresenter<TaskDetailView> {
        fun onTaskChecked(task: Task, checked: Boolean)

        fun onTaskEditButtonClicked()

        fun onTaskDeleteButtonClicked()
    }

    val taskDetailPresenter by lazy { Navigator.lookupService<Presenter>(context, CONTROLLER_TAG) }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    @TargetApi(21)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.menu_delete -> {
                deleteTask()
                return true
            }
        }
        return false
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
        textTaskDetailDescription.text = context.getString(R.string.no_data)
    }

    fun showTask(task: Task) {
        val title = task.title ?: ""
        val description = task.description ?: ""

        if (Strings.isNullOrEmpty(title)) {
            hideTitle()
        } else {
            showTitle(title)
        }

        if (Strings.isNullOrEmpty(description)) {
            hideDescription()
        } else {
            showDescription(description)
        }
        showCompletionStatus(task, task.isCompleted)
    }

    private fun showCompletionStatus(task: Task, completed: Boolean) {
        checkboxTaskDetailComplete.isChecked = completed
        checkboxTaskDetailComplete.setOnCheckedChangeListener { buttonView, isChecked ->
            taskDetailPresenter.onTaskChecked(task, isChecked)
        }
    }

    fun onTaskEditButtonClicked() {
        taskDetailPresenter.onTaskEditButtonClicked()
    }

    fun deleteTask() {
        taskDetailPresenter.onTaskDeleteButtonClicked()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        taskDetailPresenter.attachView(this)
    }

    override fun onDetachedFromWindow() {
        taskDetailPresenter.detachView(this)
        super.onDetachedFromWindow()
    }
}
