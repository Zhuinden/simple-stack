package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.second;

import android.support.v4.app.Fragment;
import android.view.View;

import com.google.auto.value.AutoValue;
import com.zhuinden.simplestackdemoexamplefragments.R;
import com.zhuinden.simplestackdemoexamplefragments.application.BaseKey;

/**
 * Created by Owner on 2017. 01. 12..
 */

@AutoValue
public abstract class SecondKey
        extends BaseKey {
    public static SecondKey create() {
        return new AutoValue_SecondKey(R.layout.path_second);
    }

    @Override
    protected Fragment createFragment() {
        return new SecondFragment();
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
