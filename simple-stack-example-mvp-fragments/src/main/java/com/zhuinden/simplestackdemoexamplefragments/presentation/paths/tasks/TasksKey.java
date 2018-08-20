package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.tasks;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;

import com.google.auto.value.AutoValue;
import com.zhuinden.simplestackdemoexamplefragments.R;
import com.zhuinden.simplestackdemoexamplefragments.application.BaseKey;

/**
 * Created by Zhuinden on 2017.01.25..
 */

@AutoValue
public abstract class TasksKey
        extends BaseKey {
    public static TasksKey create() {
        return new AutoValue_TasksKey(R.layout.path_tasks);
    }

    @Override
    protected Fragment createFragment() {
        return new TasksFragment();
    }

    @Override
    public int menu() {
        return R.menu.tasks_fragment_menu;
    }

    @Override
    public boolean isFabVisible() {
        return true;
    }

    @Override
    public int navigationViewId() {
        return R.id.list_navigation_menu_item;
    }

    @Override
    public boolean shouldShowUp() {
        return false;
    }

    @NonNull
    @Override
    public View.OnClickListener fabClickListener(@NonNull Fragment f) {
        return v -> {
            TasksFragment fragment = (TasksFragment) f;
            fragment.openAddNewTask();
        };
    }

    @Override
    public int fabDrawableIcon() {
        return R.drawable.ic_add;
    }
}
