package com.zhuinden.simpleservicesexample.presentation.paths.j;

import com.google.auto.value.AutoValue;
import com.zhuinden.simpleservicesexample.R;
import com.zhuinden.simpleservicesexample.application.Key;
import com.zhuinden.simpleservicesexample.presentation.paths.j.l.L;
import com.zhuinden.simpleservicesexample.presentation.paths.j.m.M;
import com.zhuinden.simpleservicesexample.presentation.paths.j.n.N;
import com.zhuinden.simplestack.Services;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Owner on 2017. 02. 17..
 */
@AutoValue
public abstract class J
        extends Key
        implements Services.Child, Services.Composite {
    public abstract Key parent();

    @Override
    public List<?> keys() {
        return Arrays.asList(L.create(), M.create(), N.create());
    }

    @Override
    public int layout() {
        return R.layout.path_j;
    }

    public static J create(Key parent) {
        return new AutoValue_J(parent);
    }

    @Override
    public void bindServices(Services.Builder builder) {
        builder.withService("J", "J");
    }
}
