package com.zhuinden.simplestackexamplemvvm.presentation.paths.tasks;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;

import com.google.auto.value.AutoValue;
import com.zhuinden.simplestackexamplemvvm.R;
import com.zhuinden.simplestackexamplemvvm.application.BaseFragment;
import com.zhuinden.simplestackexamplemvvm.application.BaseKey;
import com.zhuinden.simplestackexamplemvvm.application.injection.Injector;

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
    public TasksViewModel newViewModel() {
        return Injector.get().tasksViewModel();
    }

    @Override
    protected BaseFragment createFragment() {
        return new TasksFragment();
    }
}
