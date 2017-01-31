package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.taskdetail;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.zhuinden.simplestackdemoexamplemvp.R;
import com.zhuinden.simplestackdemoexamplemvp.presentation.objects.Task;
import com.zhuinden.simplestackdemoexamplemvp.util.BaseCoordinator;
import com.zhuinden.simplestackdemoexamplemvp.util.Strings;

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

    public void showTitle(@NonNull String title) {
        mDetailTitle.setVisibility(View.VISIBLE);
        mDetailTitle.setText(title);
    }

    public void hideTitle() {
        mDetailTitle.setVisibility(View.GONE);
    }

    public void showDescription(@NonNull String description) {
        mDetailDescription.setVisibility(View.VISIBLE);
        mDetailDescription.setText(description);
    }

    public void hideDescription() {
        mDetailDescription.setVisibility(View.GONE);
    }

    public void showMissingTask() {
        mDetailTitle.setText("");
        mDetailDescription.setText(getView().getContext().getString(R.string.no_data));
    }

    public void showTask(Task task) {
        String title = task.title();
        String description = task.description();

        if(Strings.isNullOrEmpty(title)) {
            hideTitle();
        } else {
            showTitle(title);
        }

        if(Strings.isNullOrEmpty(description)) {
            hideDescription();
        } else {
            showDescription(description);
        }
        showCompletionStatus(task, task.isCompleted());
    }

    private void showCompletionStatus(Task task, boolean completed) {
        mDetailCompleteStatus.setChecked(completed);
        mDetailCompleteStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                getPresenter().completeTask(task);
            } else {
                getPresenter().activateTask(task);
            }
        });
    }

    public void deleteTask() {
        taskDetailPresenter.deleteTask();
    }
}
