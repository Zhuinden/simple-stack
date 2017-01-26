package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.addoredittask;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.squareup.coordinators.Coordinator;
import com.zhuinden.simplestackdemoexamplemvp.R;
import com.zhuinden.simplestackdemoexamplemvp.application.Key;

/**
 * Created by Zhuinden on 2017.01.25..
 */

@AutoValue
public abstract class AddOrEditTaskKey
        implements Key {

    @Override
    public Coordinator newCoordinator() {
        return new AddOrEditTaskCoordinator();
    }

    public static Parcelable create() {
        return new AutoValue_AddOrEditTaskKey(R.layout.path_addoredittask);
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
}
