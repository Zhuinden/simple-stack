package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.addoredittask;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.google.auto.value.AutoValue;
import com.zhuinden.simplestackdemoexamplefragments.R;
import com.zhuinden.simplestackdemoexamplefragments.application.BaseKey;
import com.zhuinden.simplestackdemoexamplefragments.application.Key;

/**
 * Created by Zhuinden on 2017.01.25..
 */

@AutoValue
public abstract class AddOrEditTaskKey
        extends BaseKey {
    public abstract Key parent();

    public abstract String taskId();

    @Override
    public Fragment createFragment() {
        return new AddOrEditTaskFragment();
    }

    public static Parcelable create(Key parent) {
        return createWithTaskId(parent, "");
    }

    public static Parcelable createWithTaskId(Key parent, String taskId) {
        return new AutoValue_AddOrEditTaskKey(R.layout.path_addoredittask, parent, taskId);
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
    public View.OnClickListener fabClickListener(Fragment fragment) {
        return v -> {
            AddOrEditTaskFragment addOrEditTaskFragment = (AddOrEditTaskFragment) fragment;
            addOrEditTaskFragment.saveTask();
            addOrEditTaskFragment.navigateBack();
        };
    }

    @Override
    public int fabDrawableIcon() {
        return R.drawable.ic_done;
    }
}
