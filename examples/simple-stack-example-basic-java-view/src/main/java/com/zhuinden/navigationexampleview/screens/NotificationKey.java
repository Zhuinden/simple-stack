package com.zhuinden.navigationexampleview.screens;

import com.google.auto.value.AutoValue;
import com.zhuinden.navigationexampleview.R;
import com.zhuinden.navigationexampleview.core.navigation.BaseKey;

/**
 * Created by Owner on 2017. 06. 29..
 */
@AutoValue
public abstract class NotificationKey extends BaseKey {
    public static NotificationKey create() {
        return new AutoValue_NotificationKey();
    }

    @Override
    public int layout() {
        return R.layout.notification_view;
    }
}
