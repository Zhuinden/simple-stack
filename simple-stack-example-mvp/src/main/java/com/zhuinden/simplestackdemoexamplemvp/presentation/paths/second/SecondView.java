package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.second;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.squareup.coordinators.Coordinators;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.simplestackdemoexamplemvp.application.CustomApplication;

import javax.inject.Inject;

/**
 * Created by Owner on 2017. 01. 12..
 */

public class SecondView
        extends RelativeLayout
        implements Bundleable {
    public SecondView(Context context) {
        super(context);
        init(context);
    }

    public SecondView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SecondView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(21)
    public SecondView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        if(!isInEditMode()) {
            CustomApplication.get().getComponent().inject(this);
            secondKey = Backstack.getKey(context);
        }
    }

    @Inject
    Backstack backstack;

    SecondKey secondKey;

    @Override
    public Bundle toBundle() {
        SecondCoordinator coordinator = Coordinators.getCoordinator(this);
        return coordinator.toBundle();
    }

    @Override
    public void fromBundle(@Nullable Bundle bundle) {
        SecondCoordinator coordinator = Coordinators.getCoordinator(this);
        coordinator.fromBundle(bundle);
    }
}
