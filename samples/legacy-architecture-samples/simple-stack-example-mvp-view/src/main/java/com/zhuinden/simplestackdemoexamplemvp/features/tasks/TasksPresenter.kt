package com.zhuinden.simplestackdemoexamplemvp.features.tasks

import androidx.recyclerview.widget.DiffUtil
import com.jakewharton.rxrelay2.BehaviorRelay
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.Bundleable
import com.zhuinden.simplestackdemoexamplemvp.core.mvp.BasePresenter
import com.zhuinden.simplestackdemoexamplemvp.core.navigation.ViewKey
import com.zhuinden.simplestackdemoexamplemvp.core.navigation.getKey
import com.zhuinden.simplestackdemoexamplemvp.data.repository.TaskRepository
import com.zhuinden.simplestackdemoexamplemvp.domain.Task
import com.zhuinden.simplestackdemoexamplemvp.features.addoredittask.AddOrEditTaskKey
import com.zhuinden.simplestackdemoexamplemvp.features.taskdetail.TaskDetailKey
import com.zhuinden.simplestackdemoexamplemvp.util.BackstackHolder
import com.zhuinden.simplestackdemoexamplemvp.util.MessageQueue
import com.zhuinden.statebundle.StateBundle
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by Owner on 2017. 01. 27..
 */
// UNSCOPED!
class TasksPresenter @Inject constructor(
    private val backstackHolder: BackstackHolder,
    private val taskRepository: TaskRepository,
    private val messageQueue: MessageQueue
) : BasePresenter<TasksView>(), TasksView.Presenter, Bundleable {
    private val filterType: BehaviorRelay<TasksFilterType> = BehaviorRelay.createDefault(TasksFilterType.AllTasks())

    lateinit var disposable: Disposable

    override fun onAttach(view: TasksView) {
        super.onAttach(view)
        disposable = filterType
            .doOnNext { tasksFilterType -> view.setFilterLabelText(tasksFilterType.filterText) } //
            .switchMap { tasksFilterType -> tasksFilterType.filterTask(taskRepository) } //
            .observeOn(Schedulers.computation())
            .map<Pair<DiffUtil.DiffResult, List<Task>>> { tasks -> view.calculateDiff(tasks) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { pairOfDiffResultAndTasks ->
                view.showTasks(pairOfDiffResultAndTasks, filterType.value!!)
            }
        messageQueue.requestMessages(Backstack.getKey<ViewKey>(view.context), view)
    }

    override fun onDetach(view: TasksView) {
        disposable.dispose()
    }

    override fun onTaskCheckClicked(task: Task) {
        if (!task.isCompleted) {
            completeTask(task)
        } else {
            uncompleteTask(task)
        }
    }

    override fun onNoTasksAddButtonClicked() {
        openAddNewTask()
    }

    override fun onTaskRowClicked(task: Task) {
        backstackHolder.backstack.goTo(TaskDetailKey(task.id))
    }

    override fun onFilterActiveSelected() {
        setFiltering(TasksFilterType.ActiveTasks())
    }

    override fun onFilterCompletedSelected() {
        setFiltering(TasksFilterType.CompletedTasks())
    }

    override fun onFilterAllSelected() {
        setFiltering(TasksFilterType.AllTasks())
    }

    override fun onClearCompletedClicked() {
        deleteCompletedTasks()
    }

    override fun onRefreshClicked() {
        view?.isRefreshing = true
        Single.just("")
            .delay(2500, TimeUnit.MILLISECONDS)
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { ignored -> view?.isRefreshing = false }
    }

    private fun openAddNewTask() {
        backstackHolder.backstack.goTo(AddOrEditTaskKey.AddTaskKey(view!!.getKey()))
    }

    private fun completeTask(task: Task) {
        taskRepository.setTaskCompleted(task)
        view!!.showTaskMarkedComplete()
    }

    private fun uncompleteTask(task: Task) {
        taskRepository.setTaskActive(task)
        view!!.showTaskMarkedActive()
    }

    private fun deleteCompletedTasks() {
        taskRepository.deleteCompletedTasks()
        view!!.showCompletedTasksCleared()
    }

    private fun setFiltering(filterType: TasksFilterType) {
        this.filterType.accept(filterType)
    }

    override fun toBundle(): StateBundle = StateBundle().apply {
        putParcelable("FILTERING", filterType.value)
    }

    override fun fromBundle(bundle: StateBundle?) {
        bundle?.run {
            filterType.accept(getParcelable("FILTERING")!!)
        }
    }
}
