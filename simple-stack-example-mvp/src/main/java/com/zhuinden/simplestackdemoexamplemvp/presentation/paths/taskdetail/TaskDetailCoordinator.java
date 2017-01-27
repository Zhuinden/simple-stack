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
        extends BaseCoordinator<TaskDetailView> {
    @BindView(R.id.task_detail_title)
    TextView mDetailTitle;

    @BindView(R.id.task_detail_description)
    TextView mDetailDescription;

    @BindView(R.id.task_detail_complete)
    CheckBox mDetailCompleteStatus;

    @Inject
    public TaskDetailCoordinator() {
    }

    Backstack backstack;

    TaskDetailView taskDetailView;

    TaskDetailKey taskDetailKey;

    String taskId;

    Unbinder unbinder;

    @Override
    public void attachView(TaskDetailView view) {
        this.taskDetailView = view;
        unbinder = ButterKnife.bind(this, view);
        taskDetailKey = Backstack.getKey(view.getContext());
        this.taskId = taskDetailKey.taskId();
        this.backstack = Backstack.get(view.getContext());
    }

    @Override
    public void detachView(TaskDetailView view) {
        unbinder.unbind();
        this.taskDetailView = null;
    }

    public void editTask() {
        if(taskId == null || "".equals(taskId)) {
            showMissingTask();
            return;
        }
        showEditTask(taskId);
    }

    private void showEditTask(String taskId) {
        backstack.goTo(AddOrEditTaskKey.createWithTaskId(Backstack.getKey(taskDetailView.getContext()), taskId));
    }

    public void showTitle(@NonNull String title) {
        mDetailTitle.setVisibility(View.VISIBLE);
        mDetailTitle.setText(title);
    }

    public void showMissingTask() {
        mDetailTitle.setText("");
        mDetailDescription.setText(taskDetailView.getContext().getString(R.string.no_data));
    }
}
