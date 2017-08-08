package com.example.fragmenttransitions;

import android.os.Bundle;
import android.os.Parcelable;

/**
 * Created by Owner on 2017. 08. 08..
 */

public abstract class BaseKey
        implements Parcelable {
    public String getFragmentTag() {
        return toString();
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
