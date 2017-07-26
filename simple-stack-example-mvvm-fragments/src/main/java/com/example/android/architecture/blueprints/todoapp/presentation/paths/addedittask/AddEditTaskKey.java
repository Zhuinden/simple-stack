package com.example.android.architecture.blueprints.todoapp.presentation.paths.addedittask;

import android.content.res.Resources;
import android.support.annotation.Nullable;
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
public abstract class AddEditTaskKey
        extends BaseKey<AddEditTaskViewModel> {
    public static AddEditTaskKey create() {
        return create(null);
    }

    public static AddEditTaskKey create(String taskId) {
        return new AutoValue_AddEditTaskKey(taskId);
    }

    @Override
    protected boolean isFabVisible() {
        return true;
    }

    @Nullable
    public abstract String taskId();

    @Override
    public int navigationViewId() {
        return 0;
    }

    @Override
    public boolean shouldShowUp() {
        return true;
    }

    @Override
    protected void setupFab(Fragment fragment, FloatingActionButton fab) {
        fab.setImageResource(R.drawable.ic_done);
        fab.setOnClickListener(v -> ((AddEditTaskFragment) fragment).saveTask());
    }

    @Override
    public ViewModelLifecycleHelper.ViewModelCreator<AddEditTaskViewModel> getViewModelCreator() {
        return () -> Injection.get().addEditTaskViewModel();
    }

    @Nullable
    @Override
    public String title(Resources resources) {
        if(taskId() != null) {
            return resources.getString(R.string.edit_task);
        } else {
            return resources.getString(R.string.add_task);
        }
    }

    @Override
    protected BaseFragment createFragment() {
        return new AddEditTaskFragment();
    }
}
