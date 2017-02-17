package com.zhuinden.simpleservicesexample.presentation.paths.b;

import com.google.auto.value.AutoValue;

import com.zhuinden.simpleservicesexample.R;
import com.zhuinden.simpleservicesexample.application.Key;
import com.zhuinden.simpleservicesexample.presentation.paths.a.A;
import com.zhuinden.simpleservicesexample.presentation.paths.b.c.C;
import com.zhuinden.simpleservicesexample.presentation.paths.b.d.D;
import com.zhuinden.simpleservicesexample.presentation.paths.b.e.E;
import com.zhuinden.simplestack.Services;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Zhuinden on 2017.02.14..
 */

@AutoValue
public abstract class B
        extends Key
        implements Services.Composite, Services.Child {
    @Override
    public int layout() {
        return R.layout.path_b;
    }

    public static B create(A parent) {
        return new AutoValue_B(parent);
    }

    @Override
    public List<Key> keys() {
        return Arrays.asList(C.create(), D.create(), E.create());
    }

    @Override
    public void bindServices(Services.Builder builder) {
        builder.withService("B", "B");
    }
}
