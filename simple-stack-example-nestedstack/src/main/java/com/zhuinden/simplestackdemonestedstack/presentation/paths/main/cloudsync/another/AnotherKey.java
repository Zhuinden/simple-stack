package com.zhuinden.simplestackdemonestedstack.presentation.paths.main.cloudsync.another;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.zhuinden.simplestack.navigator.ViewChangeHandler;
import com.zhuinden.simplestackdemonestedstack.util.TransitionHandler;
import com.zhuinden.simplestack.HistoryBuilder;
import com.zhuinden.simplestackdemonestedstack.R;
import com.zhuinden.simplestackdemonestedstack.application.Key;
import com.zhuinden.simplestackdemonestedstack.presentation.paths.main.MainView;
import com.zhuinden.simplestackdemonestedstack.presentation.paths.main.cloudsync.another.internal.InternalKey;
import com.zhuinden.simplestackdemonestedstack.util.Child;

import java.util.List;

/**
 * Created by Zhuinden on 2017.02.19..
 */
@AutoValue
public abstract class AnotherKey
        extends Key
        implements Child {
    @Override
    public int layout() {
        return R.layout.path_another;
    }

    @Override
    public String stackIdentifier() {
        return MainView.StackType.CLOUDSYNC.name();
    }

    public static AnotherKey create(Object parent) {
        return new AutoValue_AnotherKey(parent);
    }

    @Override
    public boolean hasNestedStack() {
        return true;
    }

    @Override
    protected List<?> initialKeys() {
        return HistoryBuilder.single(InternalKey.create(this));
    }

    @NonNull
    @Override
    public ViewChangeHandler viewChangeHandler() {
        return new TransitionHandler();
    }
}
