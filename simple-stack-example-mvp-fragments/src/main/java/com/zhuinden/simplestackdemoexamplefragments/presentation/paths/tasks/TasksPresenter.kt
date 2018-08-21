package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.tasks

import android.support.v7.util.DiffUtil
import com.jakewharton.rxrelay2.BehaviorRelay
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.Bundleable
import com.zhuinden.simplestackdemoexamplefragments.application.Key
import com.zhuinden.simplestackdemoexamplefragments.data.repository.TaskRepository
import com.zhuinden.simplestackdemoexamplefragments.presentation.objects.Task
import com.zhuinden.simplestackdemoexamplefragments.presentation.paths.addoredittask.AddOrEditTaskKey
import com.zhuinden.simplestackdemoexamplefragments.presentation.paths.taskdetail.TaskDetailKey
import com.zhuinden.simplestackdemoexamplefragments.util.BasePresenter
import com.zhuinden.simplestackdemoexamplefragments.util.BaseViewContract
import com.zhuinden.statebundle.StateBundle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


/**
 * Created by Zhuinden on 2018. 08. 20.
 */
// UNSCOPED!
class TasksPresenter @Inject constructor(
    private val backstack: Backstack,
    private val taskRepository: TaskRepository
) : BasePresenter<TasksPresenter.ViewContract>(), Bundleable {
    interface ViewContract : BaseViewContract {
        fun showNoActiveTasks()
        fun showNoTasks()
        fun showNoCompletedTasks()

        fun setFilterLabelText(filterText: Int)
        fun calculateDiff(tasks: List<Task>): Pair<DiffUtil.DiffResult, List<Task>>
        fun showTasks(pairOfDiffResultAndTasks: Pair<DiffUtil.DiffResult, List<Task>>, tasksFilterType: TasksFilterType)
        fun showTaskMarkedComplete()
        fun showTaskMarkedActive()
        fun showCompletedTasksCleared()
    }

    var filterType: BehaviorRelay<TasksFilterType> = BehaviorRelay.createDefault(TasksFilterType.AllTasks())

    lateinit var subscription: Disposable

    public override fun onAttach(view: TasksPresenter.ViewContract) {
        subscription = filterType
            .doOnNext { tasksFilterType -> view.setFilterLabelText(tasksFilterType.filterText) } //
            .switchMap { tasksFilterType -> tasksFilterType.filterTask(taskRepository) } //
            .observeOn(Schedulers.computation())
            .map<Pair<DiffUtil.DiffResult, List<Task>>> { tasks -> view.calculateDiff(tasks) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { pairOfDiffResultAndTasks ->
                view.showTasks(pairOfDiffResultAndTasks, filterType.value)
            }
    }

    public override fun onDetach(view: TasksPresenter.ViewContract) {
        subscription.dispose()
    }

    fun openAddNewTask() {
        val tasksFragment = view
        val parentKey = tasksFragment!!.getKey<Key>()
        backstack.goTo(AddOrEditTaskKey.AddTaskKey(parentKey))
    }

    fun openTaskDetails(task: Task) {
        backstack.goTo(TaskDetailKey(task.id))
    }

    fun completeTask(task: Task) {
        taskRepository.setTaskCompleted(task)
        view?.showTaskMarkedComplete()
    }

    fun uncompleteTask(task: Task) {
        taskRepository.setTaskActive(task)
        view?.showTaskMarkedActive()
    }

    fun deleteCompletedTasks() {
        taskRepository.deleteCompletedTasks()
        view?.showCompletedTasksCleared()
    }

    fun setFiltering(filterType: TasksFilterType) {
        this.filterType.accept(filterType)
    }

    override fun toBundle(): StateBundle = StateBundle().apply {
        putParcelable("FILTERING", filterType.value)
    }

    override fun fromBundle(bundle: StateBundle?) {
        bundle?.run {
            filterType.accept(getParcelable("FILTERING"))
        }
    }
}
