package com.zhuinden.navigationexamplefrag;

import com.google.auto.value.AutoValue;

/**
 * Created by Owner on 2017. 06. 29..
 */
@AutoValue
public abstract class OtherKey extends BaseKey {
    public static OtherKey create() {
        return new AutoValue_OtherKey();
    }

    @Override
    protected BaseFragment createFragment() {
        return new OtherFragment();
    }
}
