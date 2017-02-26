package com.zhuinden.simplestackdemonestedstack.presentation.paths.main.cloudsync.another;

import com.google.auto.value.AutoValue;
import com.zhuinden.simplestackdemonestedstack.R;
import com.zhuinden.simplestackdemonestedstack.application.Key;
import com.zhuinden.simplestackdemonestedstack.presentation.paths.main.MainView;

/**
 * Created by Zhuinden on 2017.02.19..
 */
@AutoValue
public abstract class AnotherKey
        extends Key {
    @Override
    public int layout() {
        return R.layout.path_another;
    }

    @Override
    public String stackIdentifier() {
        return MainView.StackType.CLOUDSYNC.name();
    }

    public static AnotherKey create() {
        return new AutoValue_AnotherKey();
    }
}
