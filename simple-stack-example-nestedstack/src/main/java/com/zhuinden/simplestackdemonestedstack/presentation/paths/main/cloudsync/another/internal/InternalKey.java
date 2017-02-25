package com.zhuinden.simplestackdemonestedstack.presentation.paths.main.cloudsync.another.internal;

import com.google.auto.value.AutoValue;
import com.zhuinden.simplestackdemonestedstack.R;
import com.zhuinden.simplestackdemonestedstack.application.Key;
import com.zhuinden.simplestackdemonestedstack.application.MainActivity;

/**
 * Created by Zhuinden on 2017.02.25..
 */

@AutoValue
public abstract class InternalKey
        extends Key {
    @Override
    public int layout() {
        return R.layout.path_internal;
    }

    @Override
    public String stackIdentifier() {
        return MainActivity.StackType.CLOUDSYNC.name();
    }

    public static InternalKey create() {
        return new AutoValue_InternalKey();
    }
}