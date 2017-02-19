package com.zhuinden.simplestackdemomultistack.presentation.paths.main.cloudsync.another;

import com.google.auto.value.AutoValue;
import com.zhuinden.simplestackdemomultistack.R;
import com.zhuinden.simplestackdemomultistack.application.Key;
import com.zhuinden.simplestackdemomultistack.application.MainActivity;

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
        return MainActivity.StackType.CLOUDSYNC.name();
    }

    public static Object create() {
        return new AutoValue_AnotherKey();
    }
}
