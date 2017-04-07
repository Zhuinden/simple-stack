package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.taskdetail;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestackdemoexamplemvp.data.repository.TaskRepository;
import com.zhuinden.simplestackdemoexamplemvp.presentation.objects.Task;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.addoredittask.AddOrEditTaskKey;
import com.zhuinden.simplestackdemoexamplemvp.util.BasePresenter;
import com.zhuinden.simplestackdemoexamplemvp.util.Strings;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Owner on 2017. 01. 27..
 */

public class TaskDetailPresenter
        extends BasePresenter<TaskDetailView, TaskDetailPresenter> {
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
    protected void onAttach(TaskDetailView view) {
        taskDetailKey = Backstack.getKey(view.getContext());
        this.taskId = taskDetailKey.taskId();
        taskRepository.findTask(taskId).observeOn(AndroidSchedulers.mainThread()).subscribe(taskOptional -> {
            if(taskOptional.isPresent()) {
                task = taskOptional.get();
                view.showTask(task);
            } else {
                task = null;
                view.showMissingTask();
            }
        });
    }

    @Override
    protected void onDetach(TaskDetailView view) {

    }

    public void editTask() {
        if(Strings.isNullOrEmpty(taskId)) {
            getView().showMissingTask();
            return;
        }
        backstack.goTo(AddOrEditTaskKey.createWithTaskId(Backstack.getKey(getView().getContext()), taskId));
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
