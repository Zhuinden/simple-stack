package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.tasks;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.jakewharton.rxrelay.BehaviorRelay;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.simplestackdemoexamplemvp.data.repository.TaskRepository;
import com.zhuinden.simplestackdemoexamplemvp.presentation.objects.Task;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.addoredittask.AddOrEditTaskKey;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.taskdetail.TaskDetailKey;
import com.zhuinden.simplestackdemoexamplemvp.util.MessageQueue;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Owner on 2017. 01. 27..
 */
// UNSCOPED!
public class TasksPresenter
        implements Bundleable {
    @Inject
    public TasksPresenter() {
    }

    @Inject
    Backstack backstack;

    @Inject
    TaskRepository taskRepository;

    @Inject
    MessageQueue messageQueue;

    @Inject
    Resources resources;

    BehaviorRelay<TasksFilterType> filterType = BehaviorRelay.create(TasksFilterType.ALL_TASKS);

    Subscription subscription;

    TasksCoordinator tasksCoordinator;

    public void attach(TasksCoordinator tasksCoordinator) {
        this.tasksCoordinator = tasksCoordinator;
        subscription = filterType.asObservable() //
                .doOnNext(tasksFilterType -> tasksCoordinator.setFilterLabelText(tasksFilterType.getFilterText())) //
                .switchMap((tasksFilterType -> tasksFilterType.filterTask(taskRepository))) //
                .observeOn(Schedulers.computation())
                .map(tasks -> tasksCoordinator.calculateDiff(tasks))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pairOfDiffResultAndTasks -> {
                    if(tasksCoordinator != null) {
                        tasksCoordinator.showTasks(pairOfDiffResultAndTasks, filterType.getValue());
                    }
                });

        messageQueue.requestMessages(tasksCoordinator.getKey(), tasksCoordinator);
    }

    public void detach(TasksCoordinator tasksCoordinator) {
        subscription.unsubscribe();
        this.tasksCoordinator = null;
    }

    public void openAddNewTask() {
        backstack.goTo(AddOrEditTaskKey.create(tasksCoordinator.getKey()));
    }

    public void openTaskDetails(Task task) {
        backstack.goTo(TaskDetailKey.create(task.id()));
    }

    public void completeTask(Task task) {
        taskRepository.insertTask(task.toBuilder().setCompleted(true).build());
        tasksCoordinator.showTaskMarkedComplete();
    }

    public void uncompleteTask(Task task) {
        taskRepository.insertTask(task.toBuilder().setCompleted(false).build());
        tasksCoordinator.showTaskMarkedActive();
    }

    public void deleteCompletedTasks() {
        taskRepository.deleteCompletedTasks();
        tasksCoordinator.showCompletedTasksCleared();
    }

    public void setFiltering(TasksFilterType filterType) {
        this.filterType.call(filterType);
    }

    @Override
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("FILTERING", filterType.getValue().name());
        return bundle;
    }

    @Override
    public void fromBundle(@Nullable Bundle bundle) {
        if(bundle != null) {
            filterType.call(TasksFilterType.valueOf(bundle.getString("FILTERING")));
        }
    }
}
