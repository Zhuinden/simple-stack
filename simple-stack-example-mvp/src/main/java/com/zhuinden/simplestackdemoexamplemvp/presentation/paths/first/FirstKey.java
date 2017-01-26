package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.first;

import com.google.auto.value.AutoValue;
import com.squareup.coordinators.Coordinator;
import com.zhuinden.simplestackdemoexamplemvp.R;
import com.zhuinden.simplestackdemoexamplemvp.application.Key;

/**
 * Created by Owner on 2017. 01. 12..
 */
@AutoValue
public abstract class FirstKey implements Key {
    public static FirstKey create() {
        return new AutoValue_FirstKey(R.layout.path_first);
    }

    @Override
    public final Coordinator newCoordinator() {
        return new FirstCoordinator();
    }
}
