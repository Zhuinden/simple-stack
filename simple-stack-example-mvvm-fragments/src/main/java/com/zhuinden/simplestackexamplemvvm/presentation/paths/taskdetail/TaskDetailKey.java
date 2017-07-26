package com.zhuinden.simplestackexamplemvvm.presentation.paths.taskdetail;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;

import com.google.auto.value.AutoValue;
import com.zhuinden.simplestackexamplemvvm.R;
import com.zhuinden.simplestackexamplemvvm.application.BaseFragment;
import com.zhuinden.simplestackexamplemvvm.application.BaseKey;
import com.zhuinden.simplestackexamplemvvm.application.injection.Injection;
import com.zhuinden.simplestackexamplemvvm.core.viewmodels.ViewModelLifecycleHelper;

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
