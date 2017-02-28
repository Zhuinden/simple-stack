package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.addoredittask;

import android.support.annotation.Nullable;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.simplestack.StateBundle;
import com.zhuinden.simplestackdemoexamplemvp.R;
import com.zhuinden.simplestackdemoexamplemvp.util.BaseCoordinator;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import butterknife.Unbinder;

/**
 * Created by Owner on 2017. 01. 26..
 */

// UNSCOPED!
public class AddOrEditTaskCoordinator
        extends BaseCoordinator<AddOrEditTaskCoordinator, AddOrEditTaskPresenter>
        implements Bundleable {
    @Inject
    public AddOrEditTaskCoordinator() {
    }

    @Inject
    AddOrEditTaskPresenter addOrEditTaskPresenter;

    @OnTextChanged(R.id.add_task_title)
    public void titleChanged(Editable editable) {
        addOrEditTaskPresenter.updateTitle(editable.toString());
    }

    @OnTextChanged(R.id.add_task_description)
    public void descriptionChanged(Editable editable) {
        addOrEditTaskPresenter.updateDescription(editable.toString());
    }

    @BindView(R.id.add_task_title)
    EditText addTaskTitle;

    @BindView(R.id.add_task_description)
    EditText addTaskDescription;

    @Override
    public AddOrEditTaskPresenter getPresenter() {
        return addOrEditTaskPresenter;
    }

    @Override
    public AddOrEditTaskCoordinator getThis() {
        return this;
    }

    @Override
    protected Unbinder bindViews(View view) {
        return ButterKnife.bind(this, view);
    }

    public void saveTask() {
        addOrEditTaskPresenter.saveTask();
    }

    public void navigateBack() {
        addOrEditTaskPresenter.navigateBack();
    }

    @Override
    public StateBundle toBundle() {
        return addOrEditTaskPresenter.toBundle();
    }

    @Override
    public void fromBundle(@Nullable StateBundle bundle) {
        if(bundle != null) {
            addOrEditTaskPresenter.fromBundle(bundle);
        }
    }

    public void setTitle(String title) {
        addTaskTitle.setText(title);
    }

    public void setDescription(String description) {
        addTaskDescription.setText(description);
    }
}
