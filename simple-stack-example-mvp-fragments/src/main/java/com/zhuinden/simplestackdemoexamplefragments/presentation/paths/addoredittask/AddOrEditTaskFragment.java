package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.addoredittask;

import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import com.zhuinden.simplestackdemoexamplefragments.R;
import com.zhuinden.simplestackdemoexamplefragments.application.Injector;
import com.zhuinden.simplestackdemoexamplefragments.util.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import butterknife.Unbinder;

/**
 * Created by Owner on 2017. 01. 26..
 */

// UNSCOPED!
public class AddOrEditTaskFragment
        extends BaseFragment<AddOrEditTaskFragment, AddOrEditTaskPresenter> {
    private AddOrEditTaskPresenter addOrEditTaskPresenter;

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
    public AddOrEditTaskFragment getThis() {
        return this;
    }

    @Override
    protected Unbinder bindViews(View view) {
        return ButterKnife.bind(this, view);
    }

    @Override
    protected void injectSelf() {
        addOrEditTaskPresenter = Injector.get().addOrEditTaskPresenter();
    }

    public void saveTask() {
        addOrEditTaskPresenter.saveTask();
    }

    public void navigateBack() {
        addOrEditTaskPresenter.navigateBack();
    }

    public void setTitle(String title) {
        addTaskTitle.setText(title);
    }

    public void setDescription(String description) {
        addTaskDescription.setText(description);
    }
}
