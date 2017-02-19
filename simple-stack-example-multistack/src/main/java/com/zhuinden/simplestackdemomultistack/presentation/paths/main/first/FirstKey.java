package com.zhuinden.simplestackdemomultistack.presentation.paths.main.first;

import com.google.auto.value.AutoValue;
import com.zhuinden.simplestackdemomultistack.R;
import com.zhuinden.simplestackdemomultistack.application.Key;

/**
 * Created by Owner on 2017. 01. 12..
 */
@AutoValue
public abstract class FirstKey implements Key {
    @Override
    public int layout() {
        return R.layout.path_first;
    }

    public static FirstKey create() {
        return new AutoValue_FirstKey();
    }
}
