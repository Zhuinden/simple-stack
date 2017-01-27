package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.addoredittask;

import android.os.Parcelable;
import android.view.View;

import com.google.auto.value.AutoValue;
import com.squareup.coordinators.Coordinator;
import com.squareup.coordinators.Coordinators;
import com.zhuinden.simplestackdemoexamplemvp.R;
import com.zhuinden.simplestackdemoexamplemvp.application.Key;
import com.zhuinden.simplestackdemoexamplemvp.application.injection.SingletonComponent;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.tasks.TasksKey;

/**
 * Created by Zhuinden on 2017.01.25..
 */

@AutoValue
public abstract class AddOrEditTaskKey
        implements Key {
    public abstract TasksKey parent();

    @Override
    public Coordinator newCoordinator(SingletonComponent singletonComponent) {
        return singletonComponent.addOrEditTaskCoordinator();
    }

    public static Parcelable create(TasksKey parent) {
        return new AutoValue_AddOrEditTaskKey(R.layout.path_addoredittask, parent);
    }

    @Override
    public int menu() {
        return R.menu.empty_menu;
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
            AddOrEditTaskCoordinator addOrEditTaskCoordinator = Coordinators.getCoordinator(view);
            addOrEditTaskCoordinator.fabClicked();
        };
    }

    @Override
    public int fabDrawableIcon() {
        return R.drawable.ic_done;
    }
}
