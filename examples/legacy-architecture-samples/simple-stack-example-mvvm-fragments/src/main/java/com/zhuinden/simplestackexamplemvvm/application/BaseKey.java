package com.zhuinden.simplestackexamplemvvm.application;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;

import com.zhuinden.simplestack.ServiceBinder;
import com.zhuinden.simplestackexamplemvvm.util.ServiceProvider;

/**
 * Created by Zhuinden on 2017.07.26..
 */

public abstract class BaseKey<T>
        implements Parcelable, ServiceProvider.HasServices {
    @NonNull
    @Override
    public String getScopeTag() {
        return getFragmentTag();
    }

    @Override
    public void bindServices(@NonNull ServiceBinder serviceBinder) {
        serviceBinder.addService(getViewModelTag(), newViewModel());
    }

    protected abstract boolean isFabVisible();

    protected abstract void setupFab(Fragment fragment, FloatingActionButton fab);

    public String getFragmentTag() {
        return toString();
    }

    public String getViewModelTag() {
        return getFragmentTag() + "_VIEW_MODEL";
    }

    public abstract T newViewModel();

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
