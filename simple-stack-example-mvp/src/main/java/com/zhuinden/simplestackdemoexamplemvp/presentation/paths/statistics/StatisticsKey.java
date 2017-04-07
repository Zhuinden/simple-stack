package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.statistics;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.View;

import com.google.auto.value.AutoValue;
import com.zhuinden.simplestack.navigator.ViewChangeHandler;
import com.zhuinden.simplestack.navigator.changehandlers.SegueViewChangeHandler;
import com.zhuinden.simplestackdemoexamplemvp.R;
import com.zhuinden.simplestackdemoexamplemvp.application.Key;

/**
 * Created by Zhuinden on 2017.01.25..
 */

@AutoValue
public abstract class StatisticsKey
        implements Key {
    @NonNull
    @Override
    public ViewChangeHandler viewChangeHandler() {
        return new SegueViewChangeHandler();
    }

    public static Parcelable create() {
        return new AutoValue_StatisticsKey(R.layout.path_statistics);
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
        return R.id.statistics_navigation_menu_item;
    }

    @Override
    public boolean shouldShowUp() {
        return false;
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
