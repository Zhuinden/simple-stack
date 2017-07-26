package com.example.android.architecture.blueprints.todoapp.presentation.paths.statistics;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;

import com.example.android.architecture.blueprints.todoapp.R;
import com.example.android.architecture.blueprints.todoapp.application.BaseFragment;
import com.example.android.architecture.blueprints.todoapp.application.BaseKey;
import com.example.android.architecture.blueprints.todoapp.application.Injection;
import com.example.android.architecture.blueprints.todoapp.core.viewmodels.ViewModelLifecycleHelper;
import com.google.auto.value.AutoValue;

/**
 * Created by Zhuinden on 2017.07.26..
 */

@AutoValue
public abstract class StatisticsKey
        extends BaseKey<StatisticsViewModel> {
    @Override
    protected boolean isFabVisible() {
        return false;
    }

    @Override
    public boolean shouldShowUp() {
        return false;
    }

    @Override
    protected void setupFab(Fragment fragment, FloatingActionButton fab) {
        // do nothing
    }

    @Override
    public int navigationViewId() {
        return R.id.statistics_navigation_menu_item;
    }

    @Override
    protected BaseFragment createFragment() {
        return new StatisticsFragment();
    }

    public static StatisticsKey create() {
        return new AutoValue_StatisticsKey();
    }

    @Override
    public ViewModelLifecycleHelper.ViewModelCreator<StatisticsViewModel> getViewModelCreator() {
        return () -> Injection.get().statisticsViewModel();
    }
}
