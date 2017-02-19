package com.zhuinden.simplestackdemomultistack.presentation.paths.main.chromecast;

import com.google.auto.value.AutoValue;
import com.zhuinden.simplestackdemomultistack.R;
import com.zhuinden.simplestackdemomultistack.application.Key;
import com.zhuinden.simplestackdemomultistack.application.MainActivity;

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
        return MainActivity.StackType.CHROMECAST.name();
    }
}
