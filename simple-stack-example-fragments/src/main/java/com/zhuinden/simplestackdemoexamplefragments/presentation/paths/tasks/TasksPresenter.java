package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.tasks;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.jakewharton.rxrelay.BehaviorRelay;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.simplestack.StateBundle;
import com.zhuinden.simplestackdemoexamplefragments.application.Key;
import com.zhuinden.simplestackdemoexamplefragments.data.repository.TaskRepository;
import com.zhuinden.simplestackdemoexamplefragments.presentation.objects.Task;
import com.zhuinden.simplestackdemoexamplefragments.presentation.paths.addoredittask.AddOrEditTaskKey;
import com.zhuinden.simplestackdemoexamplefragments.presentation.paths.taskdetail.TaskDetailKey;
import com.zhuinden.simplestackdemoexamplefragments.util.BasePresenter;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Owner on 2017. 01. 27..
 */
// UNSCOPED!
public class TasksPresenter
        extends BasePresenter<TasksFragment, TasksPresenter>
        implements Bundleable {
    @Inject
    public TasksPresenter() {
    }

    @Inject
    Backstack backstack;

    @Inject
    TaskRepository taskRepository;

    @Inject
    Resources resources;

    BehaviorRelay<TasksFilterType> filterType = BehaviorRelay.create(TasksFilterType.ALL_TASKS);

    Subscription subscription;

    @Override
    public void onAttach(TasksFragment tasksFragment) {
        subscription = filterType.asObservable() //
                .doOnNext(tasksFilterType -> tasksFragment.setFilterLabelText(tasksFilterType.getFilterText())) //
                .switchMap((tasksFilterType -> tasksFilterType.filterTask(taskRepository))) //
                .observeOn(Schedulers.computation())
                .map(tasks -> tasksFragment.calculateDiff(tasks))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pairOfDiffResultAndTasks -> {
                    if(tasksFragment != null) {
                        tasksFragment.showTasks(pairOfDiffResultAndTasks, filterType.getValue());
                    }
                });
    }

    @Override
    public void onDetach(TasksFragment Fragment) {
        subscription.unsubscribe();
    }

    public void openAddNewTask() {
        TasksFragment tasksFragment = getFragment();
        Key parentKey = tasksFragment.getKey();
        backstack.goTo(AddOrEditTaskKey.create(parentKey));
    }

    public void openTaskDetails(Task task) {
        backstack.goTo(TaskDetailKey.create(task.id()));
    }

    public void completeTask(Task task) {
        taskRepository.setTaskCompleted(task);
        getFragment().showTaskMarkedComplete();
    }

    public void uncompleteTask(Task task) {
        taskRepository.setTaskActive(task);
        getFragment().showTaskMarkedActive();
    }

    public void deleteCompletedTasks() {
        taskRepository.deleteCompletedTasks();
        getFragment().showCompletedTasksCleared();
    }

    public void setFiltering(TasksFilterType filterType) {
        this.filterType.call(filterType);
    }

    @Override
    @NonNull
    public StateBundle toBundle() {
        StateBundle bundle = new StateBundle();
        bundle.putString("FILTERING", filterType.getValue().name());
        return bundle;
    }

    @Override
    public void fromBundle(@Nullable StateBundle bundle) {
        if(bundle != null) {
            filterType.call(TasksFilterType.valueOf(bundle.getString("FILTERING")));
        }
    }
}
