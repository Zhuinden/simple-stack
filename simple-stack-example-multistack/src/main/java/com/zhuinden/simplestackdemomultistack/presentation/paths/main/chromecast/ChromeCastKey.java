package com.zhuinden.simplestackdemomultistack.presentation.paths.main.chromecast;

import android.content.Context;

import com.google.auto.value.AutoValue;
import com.zhuinden.simplestack.BackstackDelegate;
import com.zhuinden.simplestackdemomultistack.R;
import com.zhuinden.simplestackdemomultistack.application.Key;
import com.zhuinden.simplestackdemomultistack.application.MainActivity;
import com.zhuinden.simplestackdemomultistack.util.ServiceLocator;

/**
 * Created by Owner on 2017. 01. 12..
 */

@AutoValue
public abstract class ChromeCastKey
        implements Key {
    @Override
    public int layout() {
        return R.layout.path_chromecast;
    }

    public static ChromeCastKey create() {
        return new AutoValue_ChromeCastKey();
    }

    @Override
    public BackstackDelegate selectDelegate(Context context) {
        return ServiceLocator.getService(context, MainActivity.StackType.CHROMECAST.name());
    }
}
