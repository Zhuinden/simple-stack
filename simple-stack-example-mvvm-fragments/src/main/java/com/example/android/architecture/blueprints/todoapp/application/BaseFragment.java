package com.example.android.architecture.blueprints.todoapp.application;

import android.support.v4.app.Fragment;

/**
 * Created by Zhuinden on 2017.07.26..
 */

public class BaseFragment
        extends Fragment {
    public final <T extends BaseKey> T getKey() {
        return getArguments() != null ? getArguments().getParcelable("KEY") : null;
    }
}
