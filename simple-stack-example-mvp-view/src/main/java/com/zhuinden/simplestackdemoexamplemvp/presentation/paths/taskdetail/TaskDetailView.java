package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.taskdetail;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhuinden.simplestackdemoexamplemvp.R;
import com.zhuinden.simplestackdemoexamplemvp.application.Injector;
import com.zhuinden.simplestackdemoexamplemvp.application.MainActivity;
import com.zhuinden.simplestackdemoexamplemvp.presentation.objects.Task;
import com.zhuinden.simplestackdemoexamplemvp.util.Strings;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Zhuinden on 2017.01.26..
 */

public class TaskDetailView
        extends RelativeLayout
        implements MainActivity.OptionsItemSelectedListener {
    public TaskDetailView(Context context) {
        super(context);
        init(context);
    }

    public TaskDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TaskDetailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(21)
    public TaskDetailView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        if(!isInEditMode()) {
            taskDetailPresenter = Injector.get()
                    .taskDetailPresenter();
        }
    }

    @BindView(R.id.task_detail_title)
    TextView detailTitle;

    @BindView(R.id.task_detail_description)
    TextView mDetailDescription;

    @BindView(R.id.task_detail_complete)
    CheckBox detailCompleteStatus;

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.menu_delete:
                deleteTask();
                return true;
        }
        return false;
    }

    TaskDetailPresenter taskDetailPresenter;

    public void editTask() {
        taskDetailPresenter.editTask();
    }

    public void showTitle(@NonNull String title) {
        detailTitle.setVisibility(View.VISIBLE);
        detailTitle.setText(title);
    }

    public void hideTitle() {
        detailTitle.setVisibility(View.GONE);
    }

    public void showDescription(@NonNull String description) {
        mDetailDescription.setVisibility(View.VISIBLE);
        mDetailDescription.setText(description);
    }

    public void hideDescription() {
        mDetailDescription.setVisibility(View.GONE);
    }

    public void showMissingTask() {
        detailTitle.setText("");
        mDetailDescription.setText(getContext().getString(R.string.no_data));
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
        detailCompleteStatus.setChecked(completed);
        detailCompleteStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                taskDetailPresenter.completeTask(task);
            } else {
                taskDetailPresenter.activateTask(task);
            }
        });
    }

    public void deleteTask() {
        taskDetailPresenter.deleteTask();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        taskDetailPresenter.attachView(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        taskDetailPresenter.detachView(this);
        super.onDetachedFromWindow();
    }
}
