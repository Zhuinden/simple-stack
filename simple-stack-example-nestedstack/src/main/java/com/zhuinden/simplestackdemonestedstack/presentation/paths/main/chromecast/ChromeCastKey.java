package com.zhuinden.simplestackdemonestedstack.presentation.paths.main.chromecast;

import com.google.auto.value.AutoValue;
import com.zhuinden.simplestackdemonestedstack.R;
import com.zhuinden.simplestackdemonestedstack.application.Key;
import com.zhuinden.simplestackdemonestedstack.presentation.paths.main.MainView;

/**
 * Created by Owner on 2017. 01. 12..
 */

@AutoValue
public abstract class ChromeCastKey
        extends Key {
    @Override
    public int layout() {
        return R.layout.path_chromecast;
    }

    public static ChromeCastKey create() {
        return new AutoValue_ChromeCastKey();
    }

    @Override
    public String stackIdentifier() {
        return MainView.StackType.CHROMECAST.name();
    }
}
