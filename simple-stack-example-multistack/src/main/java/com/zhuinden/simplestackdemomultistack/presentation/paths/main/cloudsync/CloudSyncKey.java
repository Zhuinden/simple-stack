package com.zhuinden.simplestackdemomultistack.presentation.paths.main.cloudsync;

import com.google.auto.value.AutoValue;
import com.zhuinden.simplestackdemomultistack.R;
import com.zhuinden.simplestackdemomultistack.application.Key;
import com.zhuinden.simplestackdemomultistack.application.MainActivity;

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
        return MainActivity.StackType.CLOUDSYNC.name();
    }
}
