package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.first;

import android.support.v4.app.Fragment;
import android.view.View;

import com.google.auto.value.AutoValue;
import com.zhuinden.simplestackdemoexamplefragments.R;
import com.zhuinden.simplestackdemoexamplefragments.application.BaseKey;

/**
 * Created by Owner on 2017. 01. 12..
 */
@AutoValue
public abstract class FirstKey
        extends BaseKey {
    public static FirstKey create() {
        return new AutoValue_FirstKey(R.layout.path_first);
    }

    @Override
    protected Fragment createFragment() {
        return new FirstFragment();
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
        return false;
    }

    @Override
    public View.OnClickListener fabClickListener(Fragment fragment) {
        return v -> {
        };
    }

    @Override
    public int fabDrawableIcon() {
        return 0;
    }
}
