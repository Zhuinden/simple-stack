package com.zhuinden.simplestackdemomultistack.presentation.paths.main.second;

import com.google.auto.value.AutoValue;
import com.zhuinden.simplestackdemomultistack.R;
import com.zhuinden.simplestackdemomultistack.application.Key;

/**
 * Created by Owner on 2017. 01. 12..
 */

@AutoValue
public abstract class SecondKey implements Key {
    @Override
    public int layout() {
        return R.layout.path_second;
    }

    public static SecondKey create() {
        return new AutoValue_SecondKey();
    }
}
