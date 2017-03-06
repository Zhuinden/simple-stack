package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.first;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.squareup.coordinators.Coordinators;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.simplestackdemoexamplemvp.application.CustomApplication;
import com.zhuinden.statebundle.StateBundle;

import javax.inject.Inject;

/**
 * Created by Owner on 2017. 01. 12..
 */

public class FirstView
        extends RelativeLayout
        implements Bundleable {
    private static final String TAG = "FirstView";

    public FirstView(Context context) {
        super(context);
        init(context);
    }

    public FirstView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FirstView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(21)
    public FirstView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    @Inject
    Backstack backstack;

    FirstKey firstKey;

    private void init(Context context) {
        if(!isInEditMode()) {
            CustomApplication.get().getComponent().inject(this);
            firstKey = Backstack.getKey(context);
        }
    }

    @Override
    public StateBundle toBundle() {
        FirstCoordinator firstCoordinator = Coordinators.getCoordinator(this);
        return firstCoordinator.toBundle();
    }

    @Override
    public void fromBundle(@Nullable StateBundle bundle) {
        FirstCoordinator firstCoordinator = Coordinators.getCoordinator(this);
        firstCoordinator.fromBundle(bundle);
    }
}
