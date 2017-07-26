package com.example.android.architecture.blueprints.todoapp.presentation.paths.statistics;

import com.example.android.architecture.blueprints.todoapp.application.BaseFragment;
import com.example.android.architecture.blueprints.todoapp.application.BaseKey;
import com.google.auto.value.AutoValue;

/**
 * Created by Zhuinden on 2017.07.26..
 */

@AutoValue
public abstract class StatisticsKey
        extends BaseKey {
    @Override
    protected BaseFragment createFragment() {
        return new StatisticsFragment();
    }
}
