package com.zhuinden.simplestackdemonestedstack.presentation.paths.main.cloudsync;

import com.google.auto.value.AutoValue;
import com.zhuinden.simplestackdemonestedstack.R;
import com.zhuinden.simplestackdemonestedstack.application.Key;
import com.zhuinden.simplestackdemonestedstack.presentation.paths.main.MainView;

/**
 * Created by Owner on 2017. 01. 12..
 */
@AutoValue
public abstract class CloudSyncKey
        extends Key {
    @Override
    public int layout() {
        return R.layout.path_cloudsync;
    }

    public static CloudSyncKey create() {
        return new AutoValue_CloudSyncKey();
    }

    @Override
    public String stackIdentifier() {
        return MainView.StackType.CLOUDSYNC.name();
    }
}
