package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.tasks;

import android.content.res.Resources;
import android.support.annotation.Nullable;

import com.jakewharton.rxrelay2.BehaviorRelay;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.simplestackdemoexamplemvp.data.repository.TaskRepository;
import com.zhuinden.simplestackdemoexamplemvp.presentation.objects.Task;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.addoredittask.AddOrEditTaskKey;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.taskdetail.TaskDetailKey;
import com.zhuinden.simplestackdemoexamplemvp.util.BasePresenter;
import com.zhuinden.simplestackdemoexamplemvp.util.MessageQueue;
import com.zhuinden.statebundle.StateBundle;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Owner on 2017. 01. 27..
 */
// UNSCOPED!
public class TasksPresenter
        extends BasePresenter<TasksView>
        implements Bundleable {
    private final Backstack backstack;
    private final TaskRepository taskRepository;
    private final MessageQueue messageQueue;
    private final Resources resources;

    @Inject
    public TasksPresenter(Backstack backstack, TaskRepository taskRepository, MessageQueue messageQueue, Resources resources) {
        this.backstack = backstack;
        this.taskRepository = taskRepository;
        this.messageQueue = messageQueue;
        this.resources = resources;
    }

    BehaviorRelay<TasksFilterType> filterType = BehaviorRelay.createDefault(TasksFilterType.ALL_TASKS);

    Disposable disposable;

    @Override
    public void onAttach(TasksView view) {
        disposable = filterType //
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
        disposable.dispose();
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
        this.filterType.accept(filterType);
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
            filterType.accept(TasksFilterType.valueOf(bundle.getString("FILTERING")));
        }
    }
}
