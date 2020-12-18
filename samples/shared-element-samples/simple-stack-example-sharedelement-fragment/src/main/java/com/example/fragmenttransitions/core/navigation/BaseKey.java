package com.example.fragmenttransitions.core.navigation;

import android.os.Bundle;
import android.os.Parcelable;

import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Zhuinden on 2020. 12. 18..
 */

public abstract class BaseKey extends DefaultFragmentKey {
    @NotNull
    @Override
    public String getFragmentTag() {
        return toString();
    }
}
