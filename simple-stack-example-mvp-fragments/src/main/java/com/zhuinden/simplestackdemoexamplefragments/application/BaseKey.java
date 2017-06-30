package com.zhuinden.simplestackdemoexamplefragments.application;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.zhuinden.simplestackdemoexamplefragments.util.BaseFragment;

/**
 * Created by Owner on 2017. 02. 03..
 */

public abstract class BaseKey
        implements Key {
    @Override
    public final Fragment newFragment() {
        Fragment fragment = createFragment();
        Bundle bundle = fragment.getArguments();
        if(bundle == null) {
            bundle = new Bundle();
        }
        bundle.putParcelable(BaseFragment.KEY_TAG, this);
        fragment.setArguments(bundle);
        return fragment;
    }

    protected abstract Fragment createFragment();

    @Override
    public String getFragmentTag() {
        return toString();
    }
}
