package com.example.android.architecture.blueprints.todoapp.application;

import android.os.Bundle;
import android.os.Parcelable;

/**
 * Created by Zhuinden on 2017.07.26..
 */

public abstract class BaseKey
        implements Parcelable {
    public String getFragmentTag() {
        return getClass().getName();
    }

    public final BaseFragment newFragment() {
        BaseFragment fragment = createFragment();
        Bundle bundle = fragment.getArguments();
        if(bundle == null) {
            bundle = new Bundle();
        }
        bundle.putParcelable("KEY", this);
        fragment.setArguments(bundle);
        return fragment;
    }

    protected abstract BaseFragment createFragment();
}
