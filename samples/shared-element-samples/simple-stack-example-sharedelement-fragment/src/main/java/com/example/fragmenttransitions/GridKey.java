package com.example.fragmenttransitions;

import com.google.auto.value.AutoValue;

/**
 * Created by Owner on 2017. 08. 08..
 */
@AutoValue
public abstract class GridKey
        extends BaseKey {
    @Override
    protected BaseFragment createFragment() {
        return new GridFragment();
    }

    public static GridKey create() {
        return new AutoValue_GridKey();
    }
}
