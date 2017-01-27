package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.statistics;

import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;

import com.zhuinden.simplestackdemoexamplemvp.R;
import com.zhuinden.simplestackdemoexamplemvp.util.BaseCoordinator;

import javax.inject.Inject;

import butterknife.BindView;
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
    Resources resources;

    @Inject
    StatisticsPresenter statisticsPresenter;

    @BindView(R.id.statistics)
    TextView mStatisticsTV;

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


    public void setProgressIndicator(boolean active) {
        if(active) {
            mStatisticsTV.setText(resources.getString(R.string.loading));
        } else {
            mStatisticsTV.setText("");
        }
    }

    public void showStatistics(int numberOfIncompleteTasks, int numberOfCompletedTasks) {
        if(numberOfCompletedTasks == 0 && numberOfIncompleteTasks == 0) {
            mStatisticsTV.setText(resources.getString(R.string.statistics_no_tasks));
        } else {
            String displayString = resources.getString(R.string.statistics_active_tasks) + " " + numberOfIncompleteTasks + "\n" + resources.getString(
                    R.string.statistics_completed_tasks) + " " + numberOfCompletedTasks;
            mStatisticsTV.setText(displayString);
        }
    }

    public void showLoadingStatisticsError() {
        mStatisticsTV.setText(resources.getString(R.string.statistics_error));
    }
}
