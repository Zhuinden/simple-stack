package com.zhuinden.navigationexamplefrag.screens;

import com.google.auto.value.AutoValue;
import com.zhuinden.navigationexamplefrag.core.navigation.BaseFragment;
import com.zhuinden.navigationexamplefrag.core.navigation.BaseKey;

/**
 * Created by Owner on 2017. 06. 29..
 */
@AutoValue
public abstract class NotificationKey extends BaseKey {
    public static NotificationKey create() {
        return new AutoValue_NotificationKey();
    }

    @Override
    protected BaseFragment createFragment() {
        return new NotificationFragment();
    }
}
