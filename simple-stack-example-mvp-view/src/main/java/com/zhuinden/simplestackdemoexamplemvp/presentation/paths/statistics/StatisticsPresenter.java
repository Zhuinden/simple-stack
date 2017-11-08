package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.statistics;

import com.zhuinden.simplestackdemoexamplemvp.data.repository.TaskRepository;
import com.zhuinden.simplestackdemoexamplemvp.presentation.objects.Task;
import com.zhuinden.simplestackdemoexamplemvp.util.BasePresenter;

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
        extends BasePresenter<StatisticsView> {
    private final TaskRepository tasksRepository;
        
    @Inject
    public StatisticsPresenter(TaskRepository taskRepository) {
        this.tasksRepository = taskRepository;
    }

    Disposable disposable;

    @Override
    protected void onAttach(StatisticsView view) {
        disposable = Observable.combineLatest(tasksRepository.getActiveTasks(), //
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
        disposable.dispose();
    }
}
