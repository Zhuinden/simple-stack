package com.example.android.architecture.blueprints.todoapp.presentation.paths.tasks;

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
public abstract class TasksKey
        extends BaseKey<TasksViewModel> {
    public static TasksKey create() {
        return new AutoValue_TasksKey();
    }

    @Override
    public int navigationViewId() {
        return R.id.list_navigation_menu_item;
    }

    @Override
    public boolean shouldShowUp() {
        return false;
    }

    @Override
    protected void setupFab(Fragment fragment, FloatingActionButton fab) {
        fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener(v -> ((TasksFragment) fragment).addNewTask());
    }

    @Override
    protected boolean isFabVisible() {
        return true;
    }

    @Override
    public ViewModelLifecycleHelper.ViewModelCreator<TasksViewModel> getViewModelCreator() {
        return () -> Injection.get().tasksViewModel();
    }

    @Override
    protected BaseFragment createFragment() {
        return new TasksFragment();
    }
}
