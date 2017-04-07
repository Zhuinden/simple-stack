package com.zhuinden.simplestackdemonestedstack.presentation.paths.main.cloudsync;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.zhuinden.simplestack.navigator.ViewChangeHandler;
import com.zhuinden.simplestackdemonestedstack.util.TransitionHandler;
import com.zhuinden.simplestack.HistoryBuilder;
import com.zhuinden.simplestackdemonestedstack.R;
import com.zhuinden.simplestackdemonestedstack.application.Key;
import com.zhuinden.simplestackdemonestedstack.presentation.paths.main.MainView;
import com.zhuinden.simplestackdemonestedstack.presentation.paths.main.cloudsync.another.AnotherKey;
import com.zhuinden.simplestackdemonestedstack.util.Child;

import java.util.List;

/**
 * Created by Owner on 2017. 01. 12..
 */
@AutoValue
public abstract class CloudSyncKey
        extends Key {
    @Override
    public int layout() {
        return R.layout.path_cloudsync;
    }

    public static CloudSyncKey create() {
        return new AutoValue_CloudSyncKey();
    }

    @Override
    public String stackIdentifier() {
        return MainView.StackType.CLOUDSYNC.name();
    }

    @Override
    protected List<?> initialKeys() {
        return HistoryBuilder.single(AnotherKey.create(this));
    }

    @Override
    public boolean hasNestedStack() {
        return true;
    }

    @NonNull
    @Override
    public ViewChangeHandler viewChangeHandler() {
        return new TransitionHandler();
    }
}
