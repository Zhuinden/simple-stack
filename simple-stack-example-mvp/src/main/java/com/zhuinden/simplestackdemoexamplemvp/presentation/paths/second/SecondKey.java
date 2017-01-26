package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.second;

import com.google.auto.value.AutoValue;
import com.squareup.coordinators.Coordinator;
import com.zhuinden.simplestackdemoexamplemvp.R;
import com.zhuinden.simplestackdemoexamplemvp.application.Key;

/**
 * Created by Owner on 2017. 01. 12..
 */

@AutoValue
public abstract class SecondKey implements Key {
    public static SecondKey create() {
        return new AutoValue_SecondKey(R.layout.path_second);
    }

    @Override
    public final Coordinator newCoordinator() {
        return new SecondCoordinator();
    }
}
