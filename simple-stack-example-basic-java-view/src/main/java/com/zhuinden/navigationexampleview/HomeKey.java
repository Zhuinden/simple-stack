package com.zhuinden.navigationexampleview;

import com.google.auto.value.AutoValue;

/**
 * Created by Owner on 2017. 06. 29..
 */
@AutoValue
public abstract class HomeKey extends BaseKey {
    public static HomeKey create() {
        return new AutoValue_HomeKey();
    }

    @Override
    public int layout() {
        return R.layout.home_view;
    }
}
