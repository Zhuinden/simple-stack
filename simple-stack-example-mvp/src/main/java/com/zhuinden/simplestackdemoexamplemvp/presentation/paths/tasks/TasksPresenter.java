package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.tasks;

import android.content.res.Resources;
import android.support.annotation.Nullable;

import com.jakewharton.rxrelay.BehaviorRelay;
import com.zhuinden.simplestackdemoexamplemvp.data.repository.TaskRepository;
import com.zhuinden.simplestackdemoexamplemvp.presentation.objects.Task;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.addoredittask.AddOrEditTaskKey;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.taskdetail.TaskDetailKey;
import com.zhuinden.simplestackdemoexamplemvp.util.BasePresenter;
import com.zhuinden.simplestackdemoexamplemvp.util.MessageQueue;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.statebundle.StateBundle;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Owner on 2017. 01. 27..
 */
// UNSCOPED!
public class TasksPresenter
        extends BasePresenter<TasksView, TasksPresenter>
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

    @Override
    public void onAttach(TasksView view) {
        subscription = filterType.asObservable() //
                .doOnNext(tasksFilterType -> view.setFilterLabelText(tasksFilterType.getFilterText())) //
                .switchMap((tasksFilterType -> tasksFilterType.filterTask(taskRepository))) //
                .observeOn(Schedulers.computation())
                .map(tasks -> view.calculateDiff(tasks))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pairOfDiffResultAndTasks -> {
                    if(view != null) {
                        view.showTasks(pairOfDiffResultAndTasks, filterType.getValue());
                    }
                });

        messageQueue.requestMessages(Backstack.getKey(view.getContext()), view);
    }

    @Override
    public void onDetach(TasksView view) {
        subscription.unsubscribe();
    }

    public void openAddNewTask() {
        backstack.goTo(AddOrEditTaskKey.create(Backstack.getKey(getView().getContext())));
    }

    public void openTaskDetails(Task task) {
        backstack.goTo(TaskDetailKey.create(task.id()));
    }

    public void completeTask(Task task) {
        taskRepository.setTaskCompleted(task);
        getView().showTaskMarkedComplete();
    }

    public void uncompleteTask(Task task) {
        taskRepository.setTaskActive(task);
        getView().showTaskMarkedActive();
    }

    public void deleteCompletedTasks() {
        taskRepository.deleteCompletedTasks();
        getView().showCompletedTasksCleared();
    }

    public void setFiltering(TasksFilterType filterType) {
        this.filterType.call(filterType);
    }

    @Override
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
