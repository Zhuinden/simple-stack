package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.statistics

import com.zhuinden.simplestackdemoexamplefragments.data.repository.TaskRepository
import com.zhuinden.simplestackdemoexamplefragments.util.BasePresenter
import com.zhuinden.simplestackdemoexamplefragments.util.combineTwo
import com.zhuinden.statebundle.StateBundle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Owner on 2017. 01. 27..
 */

class StatisticsPresenter @Inject constructor(
    private val taskRepository: TaskRepository
) : BasePresenter<StatisticsFragment, StatisticsPresenter>() {
    private lateinit var disposable: Disposable

    override fun onAttach(fragment: StatisticsFragment) {
        disposable = combineTwo(
            taskRepository.activeTasksWithChanges,
            taskRepository.completedTasksWithChanges)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { (activeTasks, completedTasks) ->
                fragment.showStatistics(activeTasks.size, completedTasks.size)
            }
    }

    override fun onDetach(fragment: StatisticsFragment) {
        disposable.dispose()
    }

    override fun toBundle(): StateBundle = StateBundle()

    override fun fromBundle(bundle: StateBundle?) {
    }
}
