package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.first;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.simplestack.StateBundle;
import com.zhuinden.simplestackdemoexamplemvp.R;
import com.zhuinden.simplestackdemoexamplemvp.util.BaseCoordinator;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Owner on 2017. 01. 25..
 */
// UNSCOPED!
public class FirstCoordinator
        extends BaseCoordinator<FirstCoordinator, FirstPresenter>
        implements Bundleable {
    @Inject
    public FirstCoordinator() {
    }

    @Inject
    FirstPresenter firstPresenter;

    private static final String TAG = "FirstCoordinator";

    @OnClick(R.id.first_button)
    public void clickButton(View view) {
        firstPresenter.goToSecondKey();
    }

    @Override
    public FirstPresenter getPresenter() {
        return firstPresenter;
    }

    @Override
    public FirstCoordinator getThis() {
        return this;
    }

    @Override
    protected Unbinder bindViews(View view) {
        return ButterKnife.bind(this, view);
    }

    @Override
    public StateBundle toBundle() {
        StateBundle bundle = new StateBundle();
        bundle.putString("HELLO", "WORLD");
        return bundle;
    }

    @Override
    public void fromBundle(@Nullable StateBundle bundle) {
        if(bundle != null) {
            Log.i(TAG, bundle.getString("HELLO"));
        }
    }
}
