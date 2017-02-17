package com.zhuinden.simpleservicesexample.presentation.paths.b.d;

import com.google.auto.value.AutoValue;
import com.zhuinden.simpleservicesexample.R;
import com.zhuinden.simpleservicesexample.application.Key;
import com.zhuinden.simpleservicesexample.presentation.paths.b.d.f.F;
import com.zhuinden.simpleservicesexample.presentation.paths.b.d.g.G;
import com.zhuinden.simplestack.Services;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Zhuinden on 2017.02.14..
 */

@AutoValue
public abstract class D
        extends Key
        implements Services.Composite {
    @Override
    public int layout() {
        return R.layout.path_d;
    }

    public static D create() {
        return new AutoValue_D();
    }

    @Override
    public List<Key> keys() {
        return Arrays.asList(F.create(), G.create());
    }

    @Override
    public void bindServices(Services.Builder builder) {
        builder.withService("D", "D");
    }
}
