package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.taskdetail;

import android.view.View;

import com.google.auto.value.AutoValue;
import com.squareup.coordinators.Coordinator;
import com.zhuinden.simplestackdemoexamplemvp.R;
import com.zhuinden.simplestackdemoexamplemvp.application.Key;

/**
 * Created by Zhuinden on 2017.01.25..
 */

@AutoValue
public abstract class TaskDetailKey
        implements Key {
    abstract String taskId();

    @Override
    public Coordinator newCoordinator() {
        return new TaskDetailCoordinator();
    }

    @Override
    public int menu() {
        return R.menu.empty_menu;
    }

    @Override
    public boolean isFabVisible() {
        return false;
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
        };
    }

    @Override
    public int fabDrawableIcon() {
        return 0;
    }
}
