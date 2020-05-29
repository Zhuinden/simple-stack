package com.zhuinden.navigationexamplecond.screens;

import com.google.auto.value.AutoValue;
import com.zhuinden.navigationexamplecond.core.navigation.BaseController;
import com.zhuinden.navigationexamplecond.core.navigation.BaseKey;

/**
 * Created by Owner on 2017. 06. 29..
 */
@AutoValue
public abstract class OtherKey extends BaseKey {
    public static OtherKey create() {
        return new AutoValue_OtherKey();
    }

    @Override
    protected BaseController createController() {
        return new OtherController();
    }
}
