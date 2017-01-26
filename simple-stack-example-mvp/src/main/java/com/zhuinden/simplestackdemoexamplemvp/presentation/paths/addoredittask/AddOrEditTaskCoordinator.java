package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.addoredittask;

import android.text.Editable;
import android.view.View;

import com.squareup.coordinators.Coordinator;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestackdemoexamplemvp.R;
import com.zhuinden.simplestackdemoexamplemvp.application.CustomApplication;
import com.zhuinden.simplestackdemoexamplemvp.data.repository.TaskRepository;
import com.zhuinden.simplestackdemoexamplemvp.presentation.objects.Task;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import butterknife.Unbinder;

/**
 * Created by Owner on 2017. 01. 26..
 */

public class AddOrEditTaskCoordinator
        extends Coordinator { // TODO: add bundleable
    String title;
    String description;

    @Inject
    TaskRepository taskRepository;

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

    @Override
    public void attach(View view) {
        super.attach(view);
        unbinder = ButterKnife.bind(this, view);
        backstack = Backstack.get(view.getContext());
        CustomApplication.get(view.getContext())
                .getComponent()
                .inject(this); // TODO: replace with constructor injection via SingletonComponent
    }

    @Override
    public void detach(View view) {
        unbinder.unbind();
        super.detach(view);
    }

    public void fabClicked() {
        if((title != null && !"".equals(title)) && (description != null && !"".equals(description))) {
            taskRepository.insertTask(Task.createNewActiveTask(title, description));
            backstack.goBack();
        }
    }
}
