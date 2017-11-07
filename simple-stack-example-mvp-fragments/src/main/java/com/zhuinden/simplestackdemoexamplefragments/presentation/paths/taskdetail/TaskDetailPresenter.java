package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.taskdetail;

import android.support.annotation.Nullable;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestackdemoexamplefragments.data.repository.TaskRepository;
import com.zhuinden.simplestackdemoexamplefragments.presentation.objects.Task;
import com.zhuinden.simplestackdemoexamplefragments.presentation.paths.addoredittask.AddOrEditTaskKey;
import com.zhuinden.simplestackdemoexamplefragments.util.BasePresenter;
import com.zhuinden.simplestackdemoexamplefragments.util.Strings;
import com.zhuinden.statebundle.StateBundle;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by Owner on 2017. 01. 27..
 */

public class TaskDetailPresenter
        extends BasePresenter<TaskDetailFragment, TaskDetailPresenter> {
    private final TaskRepository taskRepository;
    private final Backstack backstack;

    @Inject
    public TaskDetailPresenter(TaskRepository taskRepository, Backstack backstack) {
        this.taskRepository = taskRepository;
        this.backstack = backstack;
    }

    TaskDetailKey taskDetailKey;

    String taskId;

    Task task;

    @Override
    protected void onAttach(TaskDetailFragment coordinator) {
        taskDetailKey = coordinator.getKey();
        this.taskId = taskDetailKey.taskId();
        taskRepository.findTask(taskId).observeOn(AndroidSchedulers.mainThread()).subscribe(taskOptional -> {
            if(taskOptional.isPresent()) {
                task = taskOptional.get();
                coordinator.showTask(task);
            } else {
                task = null;
                coordinator.showMissingTask();
            }
        });
    }

    @Override
    protected void onDetach(TaskDetailFragment coordinator) {

    }

    public void editTask() {
        if(Strings.isNullOrEmpty(taskId)) {
            getFragment().showMissingTask();
            return;
        }
        backstack.goTo(AddOrEditTaskKey.createWithTaskId(getFragment().getKey(), taskId));
    }

    public void completeTask(Task task) {
        taskRepository.setTaskCompleted(task);
    }

    public void activateTask(Task task) {
        taskRepository.setTaskActive(task);
    }

    public void deleteTask() {
        if(task != null) {
            taskRepository.deleteTask(task);
            backstack.goBack();
        }
    }

    @Override
    public StateBundle toBundle() {
        return new StateBundle();
    }

    @Override
    public void fromBundle(@Nullable StateBundle bundle) {
    }
}
