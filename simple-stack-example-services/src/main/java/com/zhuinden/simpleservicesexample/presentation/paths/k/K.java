package com.zhuinden.simpleservicesexample.presentation.paths.k;

import com.google.auto.value.AutoValue;
import com.zhuinden.simpleservicesexample.R;
import com.zhuinden.simpleservicesexample.application.Key;
import com.zhuinden.simplestack.Services;

/**
 * Created by Owner on 2017. 02. 17..
 */

@AutoValue
public abstract class K
        extends Key
        implements Services.Child {
    public abstract Key parent();

    @Override
    public int layout() {
        return R.layout.path_k;
    }

    public static K create(Key parent) {
        return new AutoValue_K(parent);
    }

    @Override
    public void bindServices(Services.Builder builder) {
        builder.withService("K", "K");
    }
}
