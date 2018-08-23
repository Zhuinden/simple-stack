package com.zhuinden.navigationexamplefrag;

import com.google.auto.value.AutoValue;

/**
 * Created by Owner on 2017. 06. 29..
 */
@AutoValue
public abstract class HomeKey extends BaseKey {
    public static HomeKey create() {
        return new AutoValue_HomeKey();
    }

    @Override
    protected BaseFragment createFragment() {
        return new HomeFragment();
    }
}
