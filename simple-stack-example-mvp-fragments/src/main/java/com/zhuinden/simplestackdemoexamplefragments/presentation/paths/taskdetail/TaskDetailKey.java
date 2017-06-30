package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.taskdetail;

import android.support.v4.app.Fragment;
import android.view.View;

import com.google.auto.value.AutoValue;
import com.zhuinden.simplestackdemoexamplefragments.R;
import com.zhuinden.simplestackdemoexamplefragments.application.BaseKey;

/**
 * Created by Zhuinden on 2017.01.25..
 */

@AutoValue
public abstract class TaskDetailKey
        extends BaseKey {
    abstract String taskId();

    public static TaskDetailKey create(String taskId) {
        return new AutoValue_TaskDetailKey(R.layout.path_taskdetail, taskId);
    }

    @Override
    protected Fragment createFragment() {
        return new TaskDetailFragment();
    }

    @Override
    public int menu() {
        return R.menu.taskdetail_fragment_menu;
    }

    @Override
    public boolean isFabVisible() {
        return true;
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
    public View.OnClickListener fabClickListener(Fragment fragment) {
        return v -> {
            TaskDetailFragment coordinator = (TaskDetailFragment) fragment;
            coordinator.editTask();
        };
    }

    @Override
    public int fabDrawableIcon() {
        return R.drawable.ic_edit;
    }
}
