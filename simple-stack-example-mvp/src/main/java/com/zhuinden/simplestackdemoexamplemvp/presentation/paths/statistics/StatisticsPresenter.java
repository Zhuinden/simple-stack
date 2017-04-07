package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.statistics;

import com.zhuinden.simplestackdemoexamplemvp.data.repository.TaskRepository;
import com.zhuinden.simplestackdemoexamplemvp.presentation.objects.Task;
import com.zhuinden.simplestackdemoexamplemvp.util.BasePresenter;

import org.javatuples.Pair;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Owner on 2017. 01. 27..
 */

public class StatisticsPresenter
        extends BasePresenter<StatisticsView, StatisticsPresenter> {
    @Inject
    public StatisticsPresenter() {
    }

    @Inject
    TaskRepository tasksRepository;

    Subscription subscription;

    @Override
    protected void onAttach(StatisticsView view) {
        subscription = Observable.combineLatest(tasksRepository.getActiveTasks(), //
                tasksRepository.getCompletedTasks(), //
                (activeTasks, completedTasks) -> Pair.with(activeTasks, completedTasks)) //
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pairOfActiveAndCompletedTasks -> {
                    List<Task> activeTasks = pairOfActiveAndCompletedTasks.getValue0();
                    List<Task> completedTasks = pairOfActiveAndCompletedTasks.getValue1();
                    if(getView() != null) {
                        getView().showStatistics(activeTasks.size(), completedTasks.size());
                    }
                });
    }

    @Override
    protected void onDetach(StatisticsView view) {
        subscription.unsubscribe();
    }
}
