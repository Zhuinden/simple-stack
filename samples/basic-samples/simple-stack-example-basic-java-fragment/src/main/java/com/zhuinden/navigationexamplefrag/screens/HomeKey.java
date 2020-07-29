package com.zhuinden.navigationexamplefrag.screens;

import com.google.auto.value.AutoValue;
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey;

import androidx.fragment.app.Fragment;

/**
 * Created by Owner on 2017. 06. 29..
 */
@AutoValue
public abstract class HomeKey extends DefaultFragmentKey {
    public static HomeKey create() {
        return new AutoValue_HomeKey();
    }

    @Override
    protected Fragment instantiateFragment() {
        return new HomeFragment();
    }
}
