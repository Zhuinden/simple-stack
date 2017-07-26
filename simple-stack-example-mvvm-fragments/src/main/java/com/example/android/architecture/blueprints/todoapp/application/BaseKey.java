package com.example.android.architecture.blueprints.todoapp.application;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;

import com.example.android.architecture.blueprints.todoapp.core.viewmodels.ViewModelLifecycleHelper;

/**
 * Created by Zhuinden on 2017.07.26..
 */

public abstract class BaseKey<T>
        implements Parcelable {
    protected abstract boolean isFabVisible();

    protected abstract void setupFab(Fragment fragment, FloatingActionButton fab);

    public String getFragmentTag() {
        return toString();
    }

    public String getViewModelTag() {
        return getFragmentTag() + "_VIEW_MODEL";
    }

    public abstract ViewModelLifecycleHelper.ViewModelCreator<T> getViewModelCreator();

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

    @Nullable
    public String title(Resources resources) {
        return null;
    }

    public abstract int navigationViewId();

    public abstract boolean shouldShowUp();
}
