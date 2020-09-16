package com.zhuinden.simplestackdemoexamplefragments.features.tasks

import androidx.recyclerview.widget.DiffUtil
import com.jakewharton.rxrelay2.BehaviorRelay
import com.zhuinden.simplestack.Bundleable
import com.zhuinden.simplestackdemoexamplefragments.core.mvp.BasePresenter
import com.zhuinden.simplestackdemoexamplefragments.core.navigation.FragmentKey
import com.zhuinden.simplestackdemoexamplefragments.data.models.Task
import com.zhuinden.simplestackdemoexamplefragments.data.repository.TaskRepository
import com.zhuinden.simplestackdemoexamplefragments.features.addoredittask.AddOrEditTaskKey
import com.zhuinden.simplestackdemoexamplefragments.features.taskdetail.TaskDetailKey
import com.zhuinden.simplestackdemoexamplefragments.util.BackstackHolder
import com.zhuinden.statebundle.StateBundle
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject


/**
 * Created by Zhuinden on 2018. 08. 20.
 */
// UNSCOPED!
class TasksPresenter @Inject constructor(
    private val backstackHolder: BackstackHolder,
    private val taskRepository: TaskRepository
) : BasePresenter<TasksFragment>(), TasksFragment.Presenter, Bundleable {
    override fun onTaskCheckClicked(task: Task) {
        if (!task.isCompleted) {
            completeTask(task)
        } else {
            uncompleteTask(task)
        }
    }

    override fun onTaskRowClicked(task: Task) {
        openTaskDetails(task)
    }

    override fun onNoTasksAddButtonClicked() {
        openAddNewTask()
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
        refresh()
    }

    var filterType: BehaviorRelay<TasksFilterType> = BehaviorRelay.createDefault(TasksFilterType.AllTasks())

    lateinit var subscription: Disposable

    public override fun onAttach(view: TasksFragment) {
        subscription = filterType
            .doOnNext { tasksFilterType -> view.setFilterLabelText(tasksFilterType.filterText) } //
            .switchMap { tasksFilterType -> tasksFilterType.filterTask(taskRepository) } //
            .observeOn(Schedulers.computation())
            .map<Pair<DiffUtil.DiffResult, List<Task>>> { tasks -> view.calculateDiff(tasks) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { pairOfDiffResultAndTasks ->
                view.showTasks(pairOfDiffResultAndTasks, filterType.value!!)
            }
    }

    public override fun onDetach(view: TasksFragment) {
        subscription.dispose()
    }

    private fun openAddNewTask() {
        val tasksFragment = view
        val parentKey = tasksFragment!!.getKey<FragmentKey>()
        backstackHolder.backstack.goTo(AddOrEditTaskKey.AddTaskKey(parentKey))
    }

    private fun refresh() {
        view?.setRefreshing(true)
        Single.just("")
            .delay(2500, TimeUnit.MILLISECONDS)
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { _ ->
                view?.setRefreshing(false)
            }
    }

    private fun openTaskDetails(task: Task) {
        backstackHolder.backstack.goTo(TaskDetailKey(task.id))
    }

    private fun completeTask(task: Task) {
        taskRepository.setTaskCompleted(task)
        view?.showTaskMarkedComplete()
    }

    private fun uncompleteTask(task: Task) {
        taskRepository.setTaskActive(task)
        view?.showTaskMarkedActive()
    }

    private fun deleteCompletedTasks() {
        taskRepository.deleteCompletedTasks()
        view?.showCompletedTasksCleared()
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
