package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.statistics;

import android.support.v4.app.Fragment;
import android.view.View;

import com.google.auto.value.AutoValue;
import com.zhuinden.simplestackdemoexamplefragments.R;
import com.zhuinden.simplestackdemoexamplefragments.application.BaseKey;

/**
 * Created by Zhuinden on 2017.01.25..
 */

@AutoValue
public abstract class StatisticsKey
        extends BaseKey {
    @Override
    protected Fragment createFragment() {
        return new StatisticsFragment();
    }

    public static StatisticsKey create() {
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
    public View.OnClickListener fabClickListener(Fragment fragment) {
        return v -> {
        };
    }

    @Override
    public int fabDrawableIcon() {
        return 0;
    }
}
