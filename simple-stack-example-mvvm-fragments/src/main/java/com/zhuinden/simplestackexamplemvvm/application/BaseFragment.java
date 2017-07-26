package com.zhuinden.simplestackexamplemvvm.application;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

/**
 * Created by Zhuinden on 2017.07.26..
 */

public abstract class BaseFragment<VM>
        extends Fragment {
    @NonNull
    public final <T extends BaseKey> T getKey() {
        Bundle args = getArguments();
        if(args == null) {
            throw new IllegalStateException("Fragment cannot have null arguments.");
        }
        T key = args.getParcelable("KEY");
        if(key == null) {
            throw new IllegalStateException("Fragment cannot have null key");
        }
        return key;
    }

    public abstract void bindViewModel(VM viewModel);
}
