package com.example.android.architecture.blueprints.todoapp.presentation.paths.taskdetail;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;

import com.example.android.architecture.blueprints.todoapp.R;
import com.example.android.architecture.blueprints.todoapp.application.BaseFragment;
import com.example.android.architecture.blueprints.todoapp.application.BaseKey;
import com.example.android.architecture.blueprints.todoapp.application.Injection;
import com.example.android.architecture.blueprints.todoapp.core.viewmodels.ViewModelLifecycleHelper;
import com.google.auto.value.AutoValue;

/**
 * Created by Zhuinden on 2017.07.26..
 */

@AutoValue
public abstract class TaskDetailKey
        extends BaseKey<TaskDetailViewModel> {
    public abstract String taskId();

    @Override
    protected boolean isFabVisible() {
        return true;
    }

    @Override
    protected void setupFab(Fragment fragment, FloatingActionButton fab) {
        fab.setImageResource(R.drawable.ic_edit);
        fab.setOnClickListener(v -> ((TaskDetailFragment) fragment).startEditTask());
    }

    @Override
    public int navigationViewId() {
        return 0;
    }

    @Override
    public boolean shouldShowUp() {
        return true;
    }

    @Override
    protected BaseFragment createFragment() {
        return new TaskDetailFragment();
    }

    @Override
    public ViewModelLifecycleHelper.ViewModelCreator<TaskDetailViewModel> getViewModelCreator() {
        return () -> Injection.get().taskDetailViewModel();
    }

    public static TaskDetailKey create(String taskId) {
        return new AutoValue_TaskDetailKey(taskId);
    }
}
