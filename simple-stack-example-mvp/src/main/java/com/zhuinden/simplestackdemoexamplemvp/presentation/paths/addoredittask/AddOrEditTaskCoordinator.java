package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.addoredittask;

import android.text.Editable;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestackdemoexamplemvp.R;
import com.zhuinden.simplestackdemoexamplemvp.application.MessageQueue;
import com.zhuinden.simplestackdemoexamplemvp.data.repository.TaskRepository;
import com.zhuinden.simplestackdemoexamplemvp.presentation.objects.Task;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.tasks.TasksCoordinator;
import com.zhuinden.simplestackdemoexamplemvp.util.BaseCoordinator;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import butterknife.Unbinder;

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

    Unbinder unbinder;

    AddOrEditTaskView addOrEditTaskView;

    @Override
    public void attachView(AddOrEditTaskView view) {
        this.addOrEditTaskView = view;
        unbinder = ButterKnife.bind(this, view);
        backstack = Backstack.get(view.getContext());
    }

    @Override
    public void detachView(AddOrEditTaskView view) {
        unbinder.unbind();
    }

    public void fabClicked() {
        if((title != null && !"".equals(title)) && (description != null && !"".equals(description))) {
            taskRepository.insertTask(Task.createNewActiveTask(title, description));
            AddOrEditTaskKey addOrEditTaskKey = Backstack.getKey(addOrEditTaskView.getContext());
            messageQueue.pushMessageTo(addOrEditTaskKey.parent(), new TasksCoordinator.SavedSuccessfullyMessage());
            backstack.goBack();
        }
    }
}
