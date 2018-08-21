package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.statistics

import com.zhuinden.simplestackdemoexamplefragments.data.repository.TaskRepository
import com.zhuinden.simplestackdemoexamplefragments.util.BasePresenter
import com.zhuinden.simplestackdemoexamplefragments.util.BaseViewContract
import com.zhuinden.simplestackdemoexamplefragments.util.combineTwo
import com.zhuinden.statebundle.StateBundle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Zhuinden on 2018. 08. 20.
 */

class StatisticsPresenter @Inject constructor(
    private val taskRepository: TaskRepository
) : BasePresenter<StatisticsPresenter.ViewContract>() {
    interface ViewContract: BaseViewContract {
        fun showStatistics(numberOfIncompleteTasks: Int, numberOfCompletedTasks: Int)
    }

    private lateinit var disposable: Disposable

    override fun onAttach(view: StatisticsPresenter.ViewContract) {
        disposable = combineTwo(
            taskRepository.activeTasksWithChanges,
            taskRepository.completedTasksWithChanges)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { (activeTasks, completedTasks) ->
                view.showStatistics(activeTasks.size, completedTasks.size)
            }
    }

    override fun onDetach(view: StatisticsPresenter.ViewContract) {
        disposable.dispose()
    }

    override fun toBundle(): StateBundle = StateBundle()

    override fun fromBundle(bundle: StateBundle?) {
    }
}
