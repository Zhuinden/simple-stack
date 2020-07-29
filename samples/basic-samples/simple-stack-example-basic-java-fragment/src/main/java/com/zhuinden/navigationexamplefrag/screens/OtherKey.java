package com.zhuinden.navigationexamplefrag.screens;

import com.google.auto.value.AutoValue;
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey;

import androidx.fragment.app.Fragment;

/**
 * Created by Owner on 2017. 06. 29..
 */
@AutoValue
public abstract class OtherKey extends DefaultFragmentKey {
    public static OtherKey create() {
        return new AutoValue_OtherKey();
    }

    @Override
    protected Fragment instantiateFragment() {
        return new OtherFragment();
    }
}
