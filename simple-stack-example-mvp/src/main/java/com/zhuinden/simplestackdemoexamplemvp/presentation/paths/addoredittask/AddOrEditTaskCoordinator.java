package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.addoredittask;

import android.text.Editable;
import android.widget.EditText;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.HistoryBuilder;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestackdemoexamplemvp.R;
import com.zhuinden.simplestackdemoexamplemvp.application.MessageQueue;
import com.zhuinden.simplestackdemoexamplemvp.data.repository.TaskRepository;
import com.zhuinden.simplestackdemoexamplemvp.presentation.objects.Task;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.tasks.TasksCoordinator;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.tasks.TasksKey;
import com.zhuinden.simplestackdemoexamplemvp.util.BaseCoordinator;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Owner on 2017. 01. 26..
 */

// UNSCOPED!
public class AddOrEditTaskCoordinator
        extends BaseCoordinator<AddOrEditTaskView> { // TODO: add bundleable
    String title;
    String description;

    @Inject
    TaskRepository taskRepository;

    @Inject
    MessageQueue messageQueue;

    @Inject
    public AddOrEditTaskCoordinator() {
    }

    Backstack backstack;

    @OnTextChanged(R.id.add_task_title)
    public void titleChanged(Editable editable) {
        this.title = editable.toString();
    }

    @OnTextChanged(R.id.add_task_description)
    public void descriptionChanged(Editable editable) {
        this.description = editable.toString();
    }

    @BindView(R.id.add_task_title)
    EditText addTaskTitle;

    @BindView(R.id.add_task_description)
    EditText addTaskDescription;

    Unbinder unbinder;

    AddOrEditTaskView addOrEditTaskView;

    AddOrEditTaskKey addOrEditTaskKey;

    String taskId;

    Task task;

    @Override
    public void attachView(AddOrEditTaskView view) {
        this.addOrEditTaskView = view;
        unbinder = ButterKnife.bind(this, view);
        backstack = Backstack.get(view.getContext());
        addOrEditTaskKey = Backstack.getKey(view.getContext());
        taskId = addOrEditTaskKey.taskId();
        if(!"".equals(taskId)) {
            taskRepository.findTask(addOrEditTaskKey.taskId()).observeOn(AndroidSchedulers.mainThread()).subscribe(taskOptional -> {
                if(taskOptional.isPresent()) {
                    task = taskOptional.get();
                    this.title = task.title();
                    this.description = task.description();
                    addTaskTitle.setText(title);
                    addTaskDescription.setText(description);
                }
            });
        }
    }

    @Override
    public void detachView(AddOrEditTaskView view) {
        unbinder.unbind();
    }

    public void fabClicked() {
        if((title != null && !"".equals(title)) && (description != null && !"".equals(description))) {
            taskRepository.insertTask(task == null ? Task.createNewActiveTask(title, description) : task.toBuilder()
                    .setTitle(title)
                    .setDescription(description)
                    .build());
            AddOrEditTaskKey addOrEditTaskKey = Backstack.getKey(addOrEditTaskView.getContext());
            if(addOrEditTaskKey.parent() instanceof TasksKey) {
                messageQueue.pushMessageTo(addOrEditTaskKey.parent(), new TasksCoordinator.SavedSuccessfullyMessage());
                backstack.goBack();
            } else {
                backstack.setHistory(HistoryBuilder.from(backstack.getHistory()).removeUntil(TasksKey.create()).build(),
                        StateChange.Direction.BACKWARD);
            }
        }
    }
}
