package com.zhuinden.demo;

import android.support.v4.app.Fragment;

import com.google.auto.value.AutoValue;

/**
 * Created by Zhuinden on 2017.02.01..
 */

@AutoValue
public abstract class FirstKey
        extends BaseKey {
    public static FirstKey create() {
        return new AutoValue_FirstKey();
    }

    @Override
    public Fragment createFragment() {
        return new FirstFragment();
    }

    @Override
    public String getFragmentTag() {
        return "FirstKey";
    }
}
