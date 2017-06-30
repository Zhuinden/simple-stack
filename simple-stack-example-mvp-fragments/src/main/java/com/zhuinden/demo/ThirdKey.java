package com.zhuinden.demo;

import android.support.v4.app.Fragment;

import com.google.auto.value.AutoValue;

/**
 * Created by Zhuinden on 2017.02.01..
 */

@AutoValue
public abstract class ThirdKey
        extends BaseKey {
    public abstract String text();

    @Override
    public Fragment createFragment() {
        return ThirdFragment.create(text());
    }

    @Override
    public String getFragmentTag() {
        return "ThirdKey[" + text() + "]";
    }

    public static ThirdKey create() {
        return create("");
    }

    public static ThirdKey create(String text) {
        return new AutoValue_ThirdKey(text);
    }
}
