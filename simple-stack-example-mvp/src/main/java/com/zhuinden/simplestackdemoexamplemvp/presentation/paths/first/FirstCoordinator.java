package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.first;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.squareup.coordinators.Coordinator;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.simplestackdemoexamplemvp.R;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.second.SecondKey;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Owner on 2017. 01. 25..
 */

public class FirstCoordinator extends Coordinator implements Bundleable {
    private static final String TAG = "FirstCoordinator";

    Unbinder unbinder;

    @OnClick(R.id.first_button)
    public void clickButton(View view) {
        Backstack.get(view.getContext()).goTo(SecondKey.create());
    }

    @Override
    public void attach(View view) {
        Log.i(TAG, "Attached [" + view + "]");
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void detach(View view) {
        Log.i(TAG, "Detached [" + view + "]");
        unbinder.unbind();
    }

    @Override
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("HELLO", "WORLD");
        return bundle;
    }

    @Override
    public void fromBundle(@Nullable Bundle bundle) {
        if(bundle != null) {
            Log.i(TAG, bundle.getString("HELLO"));
        }
    }
}
