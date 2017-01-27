package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.tasks;

import android.view.View;

import com.google.auto.value.AutoValue;
import com.squareup.coordinators.Coordinator;
import com.squareup.coordinators.Coordinators;
import com.zhuinden.simplestackdemoexamplemvp.R;
import com.zhuinden.simplestackdemoexamplemvp.application.Key;
import com.zhuinden.simplestackdemoexamplemvp.application.injection.SingletonComponent;

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
    public Coordinator newCoordinator(SingletonComponent singletonComponent) {
        return singletonComponent.tasksCoordinator();
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
    public View.OnClickListener fabClickListener(View view) {
        return v -> {
            TasksCoordinator coordinator = Coordinators.getCoordinator(view);
            coordinator.openAddNewTask();
        };
    }

    @Override
    public int fabDrawableIcon() {
        return R.drawable.ic_add;
    }
}
