package com.zhuinden.simplestackexamplemvvm.presentation.paths.addedittask;

import android.content.res.Resources;
import android.support.annotation.Nullable;
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
    public AddEditTaskViewModel newViewModel() {
        return Injector.get().addEditTaskViewModel();
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
