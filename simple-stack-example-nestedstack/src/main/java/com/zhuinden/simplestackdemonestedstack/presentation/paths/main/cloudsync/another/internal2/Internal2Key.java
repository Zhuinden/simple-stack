package com.zhuinden.simplestackdemonestedstack.presentation.paths.main.cloudsync.another.internal2;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.zhuinden.simplestack.navigator.ViewChangeHandler;
import com.zhuinden.simplestackdemonestedstack.R;
import com.zhuinden.simplestackdemonestedstack.application.Key;
import com.zhuinden.simplestackdemonestedstack.presentation.paths.main.MainView;
import com.zhuinden.simplestackdemonestedstack.util.Child;
import com.zhuinden.simplestackdemonestedstack.util.TransitionHandler;

/**
 * Created by Zhuinden on 2017.02.26..
 */
@AutoValue
public abstract class Internal2Key
        extends Key
        implements Child {
    @Override
    public int layout() {
        return R.layout.path_internal2;
    }

    @Override
    public String stackIdentifier() {
        return MainView.StackType.CLOUDSYNC.name();
    }

    public static Internal2Key create(Object parent) {
        return new AutoValue_Internal2Key(parent);
    }

    @NonNull
    @Override
    public ViewChangeHandler viewChangeHandler() {
        return new TransitionHandler();
    }
}
