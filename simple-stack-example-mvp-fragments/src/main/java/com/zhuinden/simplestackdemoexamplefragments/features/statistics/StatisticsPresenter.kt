package com.zhuinden.simplestackdemoexamplefragments.features.statistics

import com.zhuinden.simplestackdemoexamplefragments.core.mvp.BasePresenter
import com.zhuinden.simplestackdemoexamplefragments.data.repository.TaskRepository
import com.zhuinden.simplestackdemoexamplefragments.util.combineTwo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Zhuinden on 2018. 08. 20.
 */

class StatisticsPresenter @Inject constructor(
    private val taskRepository: TaskRepository
) : BasePresenter<StatisticsFragment>(), StatisticsFragment.Presenter {
    private lateinit var disposable: Disposable

    override fun onAttach(view: StatisticsFragment) {
        disposable = combineTwo(
            taskRepository.activeTasksWithChanges,
            taskRepository.completedTasksWithChanges)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { (activeTasks, completedTasks) ->
                view.showStatistics(activeTasks.size, completedTasks.size)
            }
    }

    override fun onDetach(view: StatisticsFragment) {
        disposable.dispose()
    }
}
