package com.zhuinden.navigationexamplefrag;

import com.google.auto.value.AutoValue;

/**
 * Created by Owner on 2017. 06. 29..
 */
@AutoValue
public abstract class DashboardKey extends BaseKey {
    public static DashboardKey create() {
        return new AutoValue_DashboardKey();
    }

    @Override
    protected BaseFragment createFragment() {
        return new DashboardFragment();
    }
}
