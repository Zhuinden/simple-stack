package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.tasks;

import android.view.View;

import com.google.auto.value.AutoValue;
import com.squareup.coordinators.Coordinator;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestackdemoexamplemvp.R;
import com.zhuinden.simplestackdemoexamplemvp.application.Key;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.addoredittask.AddOrEditTaskKey;

/**
 * Created by Zhuinden on 2017.01.25..
 */

@AutoValue
public abstract class TasksKey
        implements Key {
    public static TasksKey create() {
        return new AutoValue_TasksKey(R.layout.path_tasks);
    }

    @Override
    public Coordinator newCoordinator() {
        return new TasksCoordinator();
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

    @Override
    public View.OnClickListener fabClickListener() {
        return v -> {
            Backstack.get(v.getContext()).goTo(AddOrEditTaskKey.create()); // TODO: call presenter
        };
    }

    @Override
    public int fabDrawableIcon() {
        return R.drawable.ic_add;
    }
}
