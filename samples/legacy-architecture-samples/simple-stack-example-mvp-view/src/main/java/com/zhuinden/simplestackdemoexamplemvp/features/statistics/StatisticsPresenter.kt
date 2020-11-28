package com.zhuinden.simplestackdemoexamplemvp.features.statistics

import com.zhuinden.rxcombinetuplekt.combineTuple
import com.zhuinden.simplestackdemoexamplemvp.core.mvp.BasePresenter
import com.zhuinden.simplestackdemoexamplemvp.data.repository.TaskRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by Owner on 2017. 01. 27..
 */

class StatisticsPresenter(
    private val tasksRepository: TaskRepository
) : BasePresenter<StatisticsView>(), StatisticsView.Presenter {
    lateinit var disposable: Disposable

    override fun onAttach(view: StatisticsView) {
        disposable = combineTuple(
            tasksRepository.activeTasksWithChanges,
            tasksRepository.completedTasksWithChanges
        ).subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { (activeTasks, completedTasks) ->
                view.showStatistics(activeTasks.size, completedTasks.size)
            }
    }

    override fun onDetach(view: StatisticsView) {
        disposable.dispose()
    }
}
