package com.zhuinden.simplestackdemoexamplefragments;

import android.support.v4.app.Fragment;

import com.google.auto.value.AutoValue;

/**
 * Created by Zhuinden on 2017.02.01..
 */

@AutoValue
public abstract class FourthKey implements Key {
    @Override
    public Fragment createFragment() {
        return new FourthFragment();
    }

    @Override
    public Class<? extends Fragment> getFragmentClass() {
        return FourthFragment.class;
    }

    @Override
    public String getFragmentTag() {
        return "FourthKey";
    }

    public static FourthKey create() {
        return new AutoValue_FourthKey();
    }
}
