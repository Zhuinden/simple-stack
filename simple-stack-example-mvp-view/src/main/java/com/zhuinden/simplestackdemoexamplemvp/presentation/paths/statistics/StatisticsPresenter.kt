package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.statistics

import com.zhuinden.simplestackdemoexamplemvp.data.repository.TaskRepository
import com.zhuinden.simplestackdemoexamplemvp.util.BasePresenter
import com.zhuinden.simplestackdemoexamplemvp.util.combineTwo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


/**
 * Created by Owner on 2017. 01. 27..
 */

class StatisticsPresenter @Inject constructor(
    private val tasksRepository: TaskRepository
) : BasePresenter<StatisticsView>() {

    lateinit var disposable: Disposable

    override fun onAttach(view: StatisticsView) {
        disposable = combineTwo(
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
