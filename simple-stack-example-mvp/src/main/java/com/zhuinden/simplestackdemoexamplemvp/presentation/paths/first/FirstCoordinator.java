package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.first;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.simplestackdemoexamplemvp.R;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.second.SecondKey;
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
        extends BaseCoordinator<FirstView>
        implements Bundleable {
    @Inject
    public FirstCoordinator() {
    }

    @Inject
    Backstack backstack;

    private static final String TAG = "FirstCoordinator";

    Unbinder unbinder;

    @OnClick(R.id.first_button)
    public void clickButton(View view) {
        backstack.goTo(SecondKey.create());
    }

    @Override
    protected Unbinder bindViews(View view) {
        return ButterKnife.bind(this, view);
    }

    @Override
    public void attachView(FirstView view) {
        Log.i(TAG, "Attached [" + view + "]");
    }

    @Override
    public void detachView(FirstView view) {
        Log.i(TAG, "Detached [" + view + "]");
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
