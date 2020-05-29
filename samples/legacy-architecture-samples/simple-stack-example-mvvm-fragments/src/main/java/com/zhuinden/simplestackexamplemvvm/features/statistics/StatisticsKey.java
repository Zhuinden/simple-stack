package com.zhuinden.simplestackexamplemvvm.features.statistics;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;

import com.google.auto.value.AutoValue;
import com.zhuinden.simplestackexamplemvvm.R;
import com.zhuinden.simplestackexamplemvvm.application.BaseFragment;
import com.zhuinden.simplestackexamplemvvm.application.BaseKey;
import com.zhuinden.simplestackexamplemvvm.application.injection.Injector;

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
    public StatisticsViewModel newViewModel() {
        return Injector.get().statisticsViewModel();
    }
}
