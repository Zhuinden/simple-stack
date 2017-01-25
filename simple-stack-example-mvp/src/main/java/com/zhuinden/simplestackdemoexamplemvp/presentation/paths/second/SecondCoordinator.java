package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.second;

import android.util.Log;
import android.view.View;

import com.squareup.coordinators.Coordinator;

/**
 * Created by Owner on 2017. 01. 25..
 */

public class SecondCoordinator extends Coordinator {
    private static final String TAG = "SecondCoordinator";

    @Override
    public void attach(View view) {
        Log.i(TAG, "Attached [" + view + "]");
    }

    @Override
    public void detach(View view) {
        Log.i(TAG, "Detached [" + view + "]");
    }
}
