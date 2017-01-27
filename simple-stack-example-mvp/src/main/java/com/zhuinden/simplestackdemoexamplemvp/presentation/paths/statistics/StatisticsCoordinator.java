package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.statistics;

import android.view.View;

import com.zhuinden.simplestackdemoexamplemvp.util.BaseCoordinator;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Owner on 2017. 01. 26..
 */
// UNSCOPED!
public class StatisticsCoordinator
        extends BaseCoordinator<StatisticsCoordinator, StatisticsPresenter> {
    @Inject
    public StatisticsCoordinator() {
    }

    @Inject
    StatisticsPresenter statisticsPresenter;

    @Override
    public StatisticsPresenter getPresenter() {
        return statisticsPresenter;
    }

    @Override
    public StatisticsCoordinator getThis() {
        return this;
    }

    @Override
    protected Unbinder bindViews(View view) {
        return ButterKnife.bind(this, view);
    }
}
