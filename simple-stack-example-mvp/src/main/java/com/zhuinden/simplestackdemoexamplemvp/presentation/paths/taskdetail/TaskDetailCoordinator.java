package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.taskdetail;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestackdemoexamplemvp.R;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.addoredittask.AddOrEditTaskKey;
import com.zhuinden.simplestackdemoexamplemvp.util.BaseCoordinator;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Owner on 2017. 01. 26..
 */
// UNSCOPED!
public class TaskDetailCoordinator
        extends BaseCoordinator<TaskDetailCoordinator, TaskDetailPresenter> {
    @BindView(R.id.task_detail_title)
    TextView mDetailTitle;

    @BindView(R.id.task_detail_description)
    TextView mDetailDescription;

    @BindView(R.id.task_detail_complete)
    CheckBox mDetailCompleteStatus;

    @Inject
    public TaskDetailCoordinator() {
    }

    @Inject
    Backstack backstack;

    @Inject
    TaskDetailPresenter taskDetailPresenter;

    @Override
    public TaskDetailPresenter getPresenter() {
        return taskDetailPresenter;
    }

    @Override
    public TaskDetailCoordinator getThis() {
        return this;
    }

    @Override
    protected Unbinder bindViews(View view) {
        return ButterKnife.bind(this, view);
    }

    public void editTask() {
        taskDetailPresenter.editTask();
    }

    public void showEditTask(String taskId) {
        backstack.goTo(AddOrEditTaskKey.createWithTaskId(getKey(), taskId));
    }

    public void showTitle(@NonNull String title) {
        mDetailTitle.setVisibility(View.VISIBLE);
        mDetailTitle.setText(title);
    }

    public void showMissingTask() {
        mDetailTitle.setText("");
        mDetailDescription.setText(getView().getContext().getString(R.string.no_data));
    }
}
