package com.zhuinden.simplestackdemonestedstack.presentation.paths.main.list;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.zhuinden.simplestackdemonestedstack.R;
import com.zhuinden.simplestackdemonestedstack.application.Key;
import com.zhuinden.simplestackdemonestedstack.application.MainActivity;

/**
 * Created by Zhuinden on 2017.02.19..
 */

@AutoValue
public abstract class ListKey
        extends Key {
    @Override
    public int layout() {
        return R.layout.path_list;
    }

    public static ListKey create() {
        return new AutoValue_ListKey();
    }

    @Override
    public String stackIdentifier() {
        return MainActivity.StackType.LIST.name();
    }
}
