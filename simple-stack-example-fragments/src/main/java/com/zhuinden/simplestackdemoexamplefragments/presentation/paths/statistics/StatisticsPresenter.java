package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.statistics;

import android.support.annotation.Nullable;

import com.zhuinden.simplestack.StateBundle;
import com.zhuinden.simplestackdemoexamplefragments.data.repository.TaskRepository;
import com.zhuinden.simplestackdemoexamplefragments.presentation.objects.Task;
import com.zhuinden.simplestackdemoexamplefragments.util.BasePresenter;

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
        extends BasePresenter<StatisticsFragment, StatisticsPresenter> {
    @Inject
    public StatisticsPresenter() {
    }

    @Inject
    TaskRepository tasksRepository;

    Subscription subscription;

    @Override
    protected void onAttach(StatisticsFragment coordinator) {
        subscription = Observable.combineLatest(tasksRepository.getActiveTasks(), //
                tasksRepository.getCompletedTasks(), //
                (activeTasks, completedTasks) -> Pair.with(activeTasks, completedTasks)) //
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pairOfActiveAndCompletedTasks -> {
                    List<Task> activeTasks = pairOfActiveAndCompletedTasks.getValue0();
                    List<Task> completedTasks = pairOfActiveAndCompletedTasks.getValue1();
                    if(getFragment() != null) {
                        getFragment().showStatistics(activeTasks.size(), completedTasks.size());
                    }
                });
    }

    @Override
    protected void onDetach(StatisticsFragment coordinator) {
        subscription.unsubscribe();
    }

    @Override
    public StateBundle toBundle() {
        return new StateBundle();
    }

    @Override
    public void fromBundle(@Nullable StateBundle bundle) {

    }
}
