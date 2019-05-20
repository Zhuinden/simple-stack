package com.zhuinden.simplestack.helpers;

import android.os.Parcel;
import android.support.annotation.NonNull;

public abstract class TestKeyWithScope
        extends TestKey
        implements HasServices {
    public TestKeyWithScope(String name) {
        super(name);
    }

    protected TestKeyWithScope(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getScopeTag() {
        return name;
    }
}
