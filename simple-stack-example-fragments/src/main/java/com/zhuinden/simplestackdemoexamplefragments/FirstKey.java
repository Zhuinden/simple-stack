package com.zhuinden.simplestackdemoexamplefragments;

import android.support.v4.app.Fragment;

import com.google.auto.value.AutoValue;

/**
 * Created by Zhuinden on 2017.02.01..
 */

@AutoValue
public abstract class FirstKey
        implements Key {
    public static FirstKey create() {
        return new AutoValue_FirstKey();
    }

    @Override
    public Class<? extends Fragment> getFragmentClass() {
        return FirstFragment.class;
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
