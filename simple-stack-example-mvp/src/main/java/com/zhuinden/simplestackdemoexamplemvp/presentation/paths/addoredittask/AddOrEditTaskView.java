package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.addoredittask;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.squareup.coordinators.Coordinators;
import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.simplestack.StateBundle;

/**
 * Created by Owner on 2017. 01. 26..
 */

public class AddOrEditTaskView
        extends ScrollView
        implements Bundleable {
    public AddOrEditTaskView(Context context) {
        super(context);
    }

    public AddOrEditTaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AddOrEditTaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public AddOrEditTaskView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public StateBundle toBundle() {
        AddOrEditTaskCoordinator coordinator = Coordinators.getCoordinator(this);
        return coordinator.toBundle();
    }

    @Override
    public void fromBundle(@Nullable StateBundle bundle) {
        AddOrEditTaskCoordinator coordinator = Coordinators.getCoordinator(this);
        coordinator.fromBundle(bundle);
    }
}
