package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.tasks

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.zhuinden.simplestackdemoexamplefragments.R
import com.zhuinden.simplestackdemoexamplefragments.application.Injector
import com.zhuinden.simplestackdemoexamplefragments.application.Key
import com.zhuinden.simplestackdemoexamplefragments.application.MainActivity
import com.zhuinden.simplestackdemoexamplefragments.presentation.objects.Task
import com.zhuinden.simplestackdemoexamplefragments.util.BaseFragment
import com.zhuinden.simplestackdemoexamplefragments.util.MessageQueue
import com.zhuinden.simplestackdemoexamplefragments.util.hide
import com.zhuinden.simplestackdemoexamplefragments.util.show
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.path_tasks.*
import org.jetbrains.anko.sdk15.listeners.onClick
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Zhuinden on 2018. 08. 20.
 */
// UNSCOPED!
class TasksFragment : BaseFragment<TasksPresenter.ViewContract, TasksPresenter>(), MessageQueue.Receiver, TasksPresenter.ViewContract {
    private val tasksPresenter = Injector.get().tasksPresenter()
    private val myResources = Injector.get().resources()
    private val messageQueue = Injector.get().messageQueue()

    lateinit var tasksAdapter: TasksAdapter

    var taskItemListener: TasksAdapter.TaskItemListener = object : TasksAdapter.TaskItemListener {
        override fun openTask(task: Task) {
            tasksPresenter.openTaskDetails(task)
        }

        override fun completeTask(task: Task) {
            tasksPresenter.completeTask(task)
        }

        override fun uncompleteTask(task: Task) {
            tasksPresenter.uncompleteTask(task)
        }
    }

    class SavedSuccessfullyMessage

    override val presenter: TasksPresenter = tasksPresenter

    override fun getThis(): TasksFragment = this

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        super.onCreateView(inflater, container, savedInstanceState).also {
            tasksAdapter = TasksAdapter(ArrayList(0), taskItemListener)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerTasks.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        recyclerTasks.adapter = tasksAdapter

        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(context!!, R.color.colorPrimary),
            ContextCompat.getColor(context!!, R.color.colorAccent),
            ContextCompat.getColor(context!!, R.color.colorPrimaryDark))
        // Set the scrolling view in the custom SwipeRefreshLayout.
        swipeRefreshLayout.setScrollUpChild(recyclerTasks)
        swipeRefreshLayout.setOnRefreshListener { this.refresh() }

        buttonNoTasksAdd.onClick {
            openAddNewTask()
        }

        messageQueue.requestMessages(getKey<Key>(), this)
    }

    fun openAddNewTask() {
        tasksPresenter.openAddNewTask()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!hidden) {
            messageQueue.requestMessages(getKey<Key>(), this)
        }
    }

    override fun onDestroyView() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(null)
        }
        super.onDestroyView()
    }

    override fun receiveMessage(message: Any) {
        if (message is SavedSuccessfullyMessage) {
            showSuccessfullySavedMessage()
        }
    }

    override fun calculateDiff(tasks: List<Task>): Pair<DiffUtil.DiffResult, List<Task>> {
        return Pair(DiffUtil.calculateDiff(TasksDiffCallback(tasksAdapter.data, tasks)), tasks)
    }

    fun hideEmptyViews() {
        containerTasks.show()
        viewNoTasks.hide()
    }

    override fun showTasks(pairOfDiffResultAndTasks: Pair<DiffUtil.DiffResult, List<Task>>, filterType: TasksFilterType) {
        val (diffResult, tasks) = pairOfDiffResultAndTasks

        tasksAdapter.data = tasks
        diffResult.dispatchUpdatesTo(tasksAdapter)
        if (tasks.isEmpty()) {
            filterType.showEmptyViews(this)
        } else {
            hideEmptyViews()
        }
    }

    fun showFilteringPopupMenu() {
        val popup = PopupMenu(context!!, MainActivity[context!!].findViewById(R.id.menu_filter))
        popup.menuInflater.inflate(R.menu.filter_tasks, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.active -> tasksPresenter.setFiltering(TasksFilterType.ACTIVE_TASKS)
                R.id.completed -> tasksPresenter.setFiltering(TasksFilterType.COMPLETED_TASKS)
                else -> tasksPresenter.setFiltering(TasksFilterType.ALL_TASKS)
            }
            //loadTasks(false); // reactive data source ftw
            true
        }

        popup.show()
    }

    fun clearCompletedTasks() {
        tasksPresenter.deleteCompletedTasks()
    }

    @SuppressLint("CheckResult")
    fun refresh() {
        swipeRefreshLayout.isRefreshing = true
        Single.just("")
            .delay(2500, TimeUnit.MILLISECONDS)
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { _ ->
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.isRefreshing = false
                }
            }
    }

    fun showNoActiveTasks() {
        showNoTasksViews(myResources.getString(R.string.no_tasks_active), R.drawable.ic_check_circle_24dp, false)
    }

    fun showNoTasks() {
        showNoTasksViews(myResources.getString(R.string.no_tasks_all), R.drawable.ic_assignment_turned_in_24dp, false)
    }

    fun showNoCompletedTasks() {
        showNoTasksViews(myResources.getString(R.string.no_tasks_completed), R.drawable.ic_verified_user_24dp, false)
    }

    override fun showTaskMarkedComplete() {
        showMessage(myResources.getString(R.string.task_marked_complete))
    }

    override fun showTaskMarkedActive() {
        showMessage(myResources.getString(R.string.task_marked_active))
    }

    override fun showCompletedTasksCleared() {
        showMessage(myResources.getString(R.string.completed_tasks_cleared))
    }

    fun showLoadingTasksError() {
        showMessage(myResources.getString(R.string.loading_tasks_error))
    }

    fun showSuccessfullySavedMessage() {
        showMessage(myResources.getString(R.string.successfully_saved_task_message))
    }

    private fun showMessage(message: String) {
        if (view != null) {
            Snackbar.make(view!!, message, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun showNoTasksViews(mainText: String, iconRes: Int, showAddView: Boolean) {
        containerTasks.hide()
        viewNoTasks.show()

        textNoTasks.text = mainText
        imageNoTasksIcon.setImageDrawable(ContextCompat.getDrawable(requireContext(), iconRes))
        buttonNoTasksAdd.visibility = if (showAddView) View.VISIBLE else View.GONE
    }

    override fun setFilterLabelText(filterText: Int) {
        textFilteringLabel.setText(filterText)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.menu_filter -> {
                showFilteringPopupMenu()
                return true
            }
            R.id.menu_clear -> {
                clearCompletedTasks()
                return true
            }
            R.id.menu_refresh -> {
                refresh()
                return true
            }
        }
        return false
    }
}
