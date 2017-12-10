package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.statistics;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zhuinden.simplestackdemoexamplefragments.data.repository.TaskRepository;
import com.zhuinden.simplestackdemoexamplefragments.presentation.objects.Task;
import com.zhuinden.simplestackdemoexamplefragments.util.BasePresenter;
import com.zhuinden.statebundle.StateBundle;

import org.javatuples.Pair;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Owner on 2017. 01. 27..
 */

public class StatisticsPresenter
        extends BasePresenter<StatisticsFragment, StatisticsPresenter> {
    private final TaskRepository taskRepository;

    @Inject
    public StatisticsPresenter(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    Disposable disposable;

    @Override
    protected void onAttach(StatisticsFragment coordinator) {
        disposable = Observable.combineLatest(taskRepository.getActiveTasksWithChanges(),//
                                              taskRepository.getCompletedTasksWithChanges(),//
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
        disposable.dispose();
    }

    @Override
    @NonNull
    public StateBundle toBundle() {
        return new StateBundle();
    }

    @Override
    public void fromBundle(@Nullable StateBundle bundle) {

    }
}
