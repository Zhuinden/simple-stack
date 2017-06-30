package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.statistics;

import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;

import com.zhuinden.simplestackdemoexamplefragments.R;
import com.zhuinden.simplestackdemoexamplefragments.application.Injector;
import com.zhuinden.simplestackdemoexamplefragments.util.BaseFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Owner on 2017. 01. 26..
 */
// UNSCOPED!
public class StatisticsFragment
        extends BaseFragment<StatisticsFragment, StatisticsPresenter> {
    public StatisticsFragment() {
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
    public StatisticsFragment getThis() {
        return this;
    }

    @Override
    protected Unbinder bindViews(View view) {
        return ButterKnife.bind(this, view);
    }

    @Override
    protected void injectSelf() {
        Injector.get().inject(this);
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
