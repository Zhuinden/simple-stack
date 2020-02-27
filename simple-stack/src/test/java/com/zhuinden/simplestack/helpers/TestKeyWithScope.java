package com.zhuinden.simplestack.helpers;

import android.os.Parcel;

import javax.annotation.Nonnull;

public abstract class TestKeyWithScope
        extends TestKey
        implements HasServices {
    public TestKeyWithScope(String name) {
        super(name);
    }

    protected TestKeyWithScope(Parcel in) {
        super(in);
    }

    @Nonnull
    @Override
    public String getScopeTag() {
        return name;
    }
}
