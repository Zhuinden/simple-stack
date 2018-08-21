package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.tasks

import android.support.v7.util.DiffUtil
import com.jakewharton.rxrelay2.BehaviorRelay
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.Bundleable
import com.zhuinden.simplestackdemoexamplemvp.application.Key
import com.zhuinden.simplestackdemoexamplemvp.data.repository.TaskRepository
import com.zhuinden.simplestackdemoexamplemvp.presentation.objects.Task
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.addoredittask.AddOrEditTaskKey
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.taskdetail.TaskDetailKey
import com.zhuinden.simplestackdemoexamplemvp.util.BasePresenter
import com.zhuinden.simplestackdemoexamplemvp.util.MessageQueue
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
    private val messageQueue: MessageQueue
) : BasePresenter<TasksView>(), Bundleable {

    private val filterType: BehaviorRelay<TasksFilterType> = BehaviorRelay.createDefault(TasksFilterType.AllTasks())

    lateinit var disposable: Disposable

    public override fun onAttach(view: TasksView) {
        disposable = filterType
            .doOnNext { tasksFilterType -> view.setFilterLabelText(tasksFilterType.filterText) } //
            .switchMap { tasksFilterType -> tasksFilterType.filterTask(taskRepository) } //
            .observeOn(Schedulers.computation())
            .map<Pair<DiffUtil.DiffResult, List<Task>>> { tasks -> view.calculateDiff(tasks) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { pairOfDiffResultAndTasks ->
                view.showTasks(pairOfDiffResultAndTasks, filterType.value)
            }
        messageQueue.requestMessages(Backstack.getKey<Key>(view.context), view)
    }

    public override fun onDetach(view: TasksView) {
        disposable.dispose()
    }

    fun openAddNewTask() {
        backstack.goTo(AddOrEditTaskKey.create(Backstack.getKey<Key>(view!!.context)))
    }

    fun openTaskDetails(task: Task) {
        backstack.goTo(TaskDetailKey.create(task.id))
    }

    fun completeTask(task: Task) {
        taskRepository.setTaskCompleted(task)
        view!!.showTaskMarkedComplete()
    }

    fun uncompleteTask(task: Task) {
        taskRepository.setTaskActive(task)
        view!!.showTaskMarkedActive()
    }

    fun deleteCompletedTasks() {
        taskRepository.deleteCompletedTasks()
        view!!.showCompletedTasksCleared()
    }

    fun setFiltering(filterType: TasksFilterType) {
        this.filterType.accept(filterType)
    }

    override fun toBundle(): StateBundle = StateBundle().apply {
        putParcelable("FILTERING", filterType.value)
    }

    override fun fromBundle(bundle: StateBundle?) {
        bundle?.run{
            filterType.accept(getParcelable("FILTERING"))
        }
    }
}
