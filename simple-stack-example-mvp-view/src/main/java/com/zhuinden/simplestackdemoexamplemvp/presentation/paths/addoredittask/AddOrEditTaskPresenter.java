package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.addoredittask;

import android.support.annotation.Nullable;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.simplestack.History;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestackdemoexamplemvp.data.repository.TaskRepository;
import com.zhuinden.simplestackdemoexamplemvp.presentation.objects.Task;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.tasks.TasksKey;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.tasks.TasksView;
import com.zhuinden.simplestackdemoexamplemvp.util.BasePresenter;
import com.zhuinden.simplestackdemoexamplemvp.util.MessageQueue;
import com.zhuinden.simplestackdemoexamplemvp.util.Strings;
import com.zhuinden.statebundle.StateBundle;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;


/**
 * Created by Owner on 2017. 01. 27..
 */
// UNSCOPED
public class AddOrEditTaskPresenter
        extends BasePresenter<AddOrEditTaskView>
        implements Bundleable {
    String title;
    String description;

    private final TaskRepository taskRepository;
    private final MessageQueue messageQueue;
    private final Backstack backstack;

    @Inject
    public AddOrEditTaskPresenter(TaskRepository taskRepository, MessageQueue messageQueue, Backstack backstack) {
        this.taskRepository = taskRepository;
        this.messageQueue = messageQueue;
        this.backstack = backstack;
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
    protected void onAttach(AddOrEditTaskView view) {
        AddOrEditTaskKey addOrEditTaskKey = Backstack.getKey(view.getContext());
        taskId = addOrEditTaskKey.taskId();
        if(!Strings.isNullOrEmpty(taskId)) {
            taskRepository.findTask(addOrEditTaskKey.taskId()).observeOn(AndroidSchedulers.mainThread()).subscribe(taskOptional -> {
                if(taskOptional.isPresent()) {
                    task = taskOptional.get();
                    if(this.title == null || this.description == null) {
                        this.title = task.title();
                        this.description = task.description();
                        view.setTitle(title);
                        view.setDescription(description);
                    }
                }
            });
        }
    }

    @Override
    protected void onDetach(AddOrEditTaskView view) {

    }

    public StateBundle toBundle() {
        StateBundle bundle = new StateBundle();
        bundle.putString("title", title);
        bundle.putString("description", description);
        return bundle;
    }

    @Override
    public void fromBundle(@Nullable StateBundle bundle) {
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
        AddOrEditTaskKey addOrEditTaskKey = Backstack.getKey(getView().getContext());
        if(addOrEditTaskKey.parent() instanceof TasksKey) {
            messageQueue.pushMessageTo(addOrEditTaskKey.parent(), new TasksView.SavedSuccessfullyMessage());
            backstack.goBack();
        } else {
            backstack.setHistory(History.builderFrom(backstack).removeUntil(TasksKey.create()).build(), StateChange.BACKWARD);
        }
    }
}
