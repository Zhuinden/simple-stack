package com.zhuinden.simpleservicesexample.presentation.paths.j.m;

import com.google.auto.value.AutoValue;
import com.zhuinden.simpleservicesexample.application.Key;
import com.zhuinden.simplestack.Services;

/**
 * Created by Owner on 2017. 02. 17..
 */
@AutoValue
public abstract class M
        extends Key {
    @Override
    public int layout() {
        return 0;
    }

    public static M create() {
        return new AutoValue_M();
    }

    @Override
    public void bindServices(Services.Builder builder) {
        builder.withService("M", "M");
    }
}
