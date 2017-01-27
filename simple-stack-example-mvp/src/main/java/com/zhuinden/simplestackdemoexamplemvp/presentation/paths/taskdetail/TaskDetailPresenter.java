package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.taskdetail;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestackdemoexamplemvp.data.repository.TaskRepository;
import com.zhuinden.simplestackdemoexamplemvp.presentation.objects.Task;
import com.zhuinden.simplestackdemoexamplemvp.util.BasePresenter;
import com.zhuinden.simplestackdemoexamplemvp.util.Strings;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Owner on 2017. 01. 27..
 */

public class TaskDetailPresenter
        extends BasePresenter<TaskDetailCoordinator, TaskDetailPresenter> {
    @Inject
    public TaskDetailPresenter() {
    }

    @Inject
    TaskRepository taskRepository;

    @Inject
    Backstack backstack;

    TaskDetailKey taskDetailKey;

    String taskId;

    Task task;

    @Override
    protected void onAttach(TaskDetailCoordinator coordinator) {
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
    protected void onDetach(TaskDetailCoordinator coordinator) {

    }

    public void editTask() {
        if(Strings.isNullOrEmpty(taskId)) {
            getCoordinator().showMissingTask();
            return;
        }
        getCoordinator().showEditTask(taskId);
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
}
