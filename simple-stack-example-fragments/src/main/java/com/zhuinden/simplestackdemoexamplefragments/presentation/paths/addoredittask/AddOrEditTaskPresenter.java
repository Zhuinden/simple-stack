package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.addoredittask;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.simplestack.HistoryBuilder;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestackdemoexamplefragments.data.repository.TaskRepository;
import com.zhuinden.simplestackdemoexamplefragments.presentation.objects.Task;
import com.zhuinden.simplestackdemoexamplefragments.presentation.paths.tasks.TasksFragment;
import com.zhuinden.simplestackdemoexamplefragments.presentation.paths.tasks.TasksKey;
import com.zhuinden.simplestackdemoexamplefragments.util.BasePresenter;
import com.zhuinden.simplestackdemoexamplefragments.util.MessageQueue;
import com.zhuinden.simplestackdemoexamplefragments.util.Strings;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Owner on 2017. 01. 27..
 */
// UNSCOPED
public class AddOrEditTaskPresenter
        extends BasePresenter<AddOrEditTaskFragment, AddOrEditTaskPresenter>
        implements Bundleable {
    String title;
    String description;

    @Inject
    TaskRepository taskRepository;

    @Inject
    MessageQueue messageQueue;

    @Inject
    Backstack backstack;

    @Inject
    public AddOrEditTaskPresenter() {
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    String taskId;

    Task task;

    @Override
    protected void onAttach(AddOrEditTaskFragment coordinator) {
        AddOrEditTaskKey addOrEditTaskKey = coordinator.getKey();
        taskId = addOrEditTaskKey.taskId();
        if(!Strings.isNullOrEmpty(taskId)) {
            taskRepository.findTask(addOrEditTaskKey.taskId()).observeOn(AndroidSchedulers.mainThread()).subscribe(taskOptional -> {
                if(taskOptional.isPresent()) {
                    task = taskOptional.get();
                    if(this.title == null || this.description == null) {
                        this.title = task.title();
                        this.description = task.description();
                        coordinator.setTitle(title);
                        coordinator.setDescription(description);
                    }
                }
            });
        }
    }

    @Override
    protected void onDetach(AddOrEditTaskFragment coordinator) {

    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("description", description);
        return bundle;
    }

    @Override
    public void fromBundle(@Nullable Bundle bundle) {
        if(bundle != null) {
            title = bundle.getString("title");
            description = bundle.getString("description");
        }
    }

    public void saveTask() {
        if(!Strings.isNullOrEmpty(title) && !Strings.isNullOrEmpty(description)) {
            taskRepository.insertTask(task == null ? Task.createNewActiveTask(title, description) : task.toBuilder()
                    .setTitle(title)
                    .setDescription(description)
                    .build());
        }
    }

    public void navigateBack() {
        AddOrEditTaskKey addOrEditTaskKey = getFragment().getKey();
        if(addOrEditTaskKey.parent() instanceof TasksKey) {
            messageQueue.pushMessageTo(addOrEditTaskKey.parent(), new TasksFragment.SavedSuccessfullyMessage());
            backstack.goBack();
        } else {
            backstack.setHistory(HistoryBuilder.from(backstack.getHistory()).removeUntil(TasksKey.create()).build(), StateChange.BACKWARD);
        }
    }
}
