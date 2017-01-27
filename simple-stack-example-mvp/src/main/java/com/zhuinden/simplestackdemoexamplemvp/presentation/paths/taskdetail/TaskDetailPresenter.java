package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.taskdetail;

import com.zhuinden.simplestackdemoexamplemvp.util.BasePresenter;

import javax.inject.Inject;

/**
 * Created by Owner on 2017. 01. 27..
 */

public class TaskDetailPresenter
        extends BasePresenter<TaskDetailCoordinator, TaskDetailPresenter> {
    @Inject
    public TaskDetailPresenter() {
    }

    TaskDetailKey taskDetailKey;

    String taskId;

    @Override
    protected void onAttach(TaskDetailCoordinator coordinator) {
        taskDetailKey = coordinator.getKey();
        this.taskId = taskDetailKey.taskId();
    }

    @Override
    protected void onDetach(TaskDetailCoordinator coordinator) {

    }

    public void editTask() {
        if(taskId == null || "".equals(taskId)) {
            getCoordinator().showMissingTask();
            return;
        }
        getCoordinator().showEditTask(taskId);
    }
}
