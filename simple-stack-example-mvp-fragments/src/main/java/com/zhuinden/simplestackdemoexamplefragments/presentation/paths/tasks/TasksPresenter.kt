package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.tasks

import android.content.res.Resources
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
import com.zhuinden.statebundle.StateBundle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


/**
 * Created by Owner on 2017. 01. 27..
 */
// UNSCOPED!
class TasksPresenter @Inject constructor(
    private val backstack: Backstack, 
    private val taskRepository: TaskRepository, 
    private val resources: Resources
) : BasePresenter<TasksFragment, TasksPresenter>(), Bundleable {

    var filterType = BehaviorRelay.createDefault(TasksFilterType.ALL_TASKS)

    lateinit var subscription: Disposable

    public override fun onAttach(tasksFragment: TasksFragment) {
        subscription = filterType //
            .doOnNext { tasksFilterType -> tasksFragment.setFilterLabelText(tasksFilterType.filterText) } //
            .switchMap { tasksFilterType -> tasksFilterType.filterTask(taskRepository) } //
            .observeOn(Schedulers.computation())
            .map<Pair<DiffUtil.DiffResult, List<Task>>> { tasks -> tasksFragment.calculateDiff(tasks) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { pairOfDiffResultAndTasks ->
                tasksFragment.showTasks(pairOfDiffResultAndTasks, filterType.value)
            }
    }

    public override fun onDetach(Fragment: TasksFragment) {
        subscription.dispose()
    }

    fun openAddNewTask() {
        val tasksFragment = fragment
        val parentKey = tasksFragment!!.getKey<Key>()
        backstack.goTo(AddOrEditTaskKey.create(parentKey))
    }

    fun openTaskDetails(task: Task) {
        backstack.goTo(TaskDetailKey.create(task.id))
    }

    fun completeTask(task: Task) {
        taskRepository.setTaskCompleted(task)
        fragment?.showTaskMarkedComplete()
    }

    fun uncompleteTask(task: Task) {
        taskRepository.setTaskActive(task)
        fragment?.showTaskMarkedActive()
    }

    fun deleteCompletedTasks() {
        taskRepository.deleteCompletedTasks()
        fragment?.showCompletedTasksCleared()
    }

    fun setFiltering(filterType: TasksFilterType) {
        this.filterType.accept(filterType)
    }

    override fun toBundle(): StateBundle = StateBundle().apply {
        putString("FILTERING", filterType.value.name)
    }

    override fun fromBundle(bundle: StateBundle?) {
        bundle?.run {
            filterType.accept(TasksFilterType.valueOf(getString("FILTERING")!!))
        }
    }
}
