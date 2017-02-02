package com.zhuinden.simplestackdemoexamplefragments;

import android.support.v4.app.Fragment;

import com.google.auto.value.AutoValue;

/**
 * Created by Zhuinden on 2017.02.01..
 */

@AutoValue
public abstract class ThirdKey implements Key {
    public abstract String text();

    @Override
    public Fragment createFragment() {
        return ThirdFragment.create(text());
    }

    @Override
    public String getFragmentTag() {
        return "ThirdKey[" + text() + "]";
    }

    @Override
    public Class<? extends Fragment> getFragmentClass() {
        return ThirdFragment.class;
    }

    public static ThirdKey create() {
        return create("");
    }

    public static ThirdKey create(String text) {
        return new AutoValue_ThirdKey(text);
    }
}
