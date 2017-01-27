package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.taskdetail;

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
public abstract class TaskDetailKey
        implements Key {
    abstract String taskId();

    public static TaskDetailKey create(String taskId) {
        return new AutoValue_TaskDetailKey(R.layout.path_taskdetail, taskId);
    }

    @Override
    public Coordinator newCoordinator(SingletonComponent singletonComponent) {
        return singletonComponent.taskDetailCoordinator();
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
    public View.OnClickListener fabClickListener(View view) {
        return v -> {
            TaskDetailCoordinator coordinator = Coordinators.getCoordinator(view);
            coordinator.editTask();
        };
    }

    @Override
    public int fabDrawableIcon() {
        return R.drawable.ic_edit;
    }
}
