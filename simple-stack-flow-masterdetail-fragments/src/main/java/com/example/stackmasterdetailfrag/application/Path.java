package com.example.stackmasterdetailfrag.application;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;

import com.example.stackmasterdetailfrag.ViewHostFragment;

/**
 * Created by Zhuinden on 2017.04.08..
 */

public abstract class Path
        implements Parcelable {
    public abstract String getTitle();

    @LayoutRes
    public abstract int layout();

    public String getFragmentTag() {
        return toString();
    }

    public final Fragment createFragment() {
        Fragment fragment = new ViewHostFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ViewHostFragment.KEY_TAG, this);
        fragment.setArguments(bundle);
        return fragment;
    }
}
