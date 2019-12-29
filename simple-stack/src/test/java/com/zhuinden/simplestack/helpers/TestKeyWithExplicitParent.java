package com.zhuinden.simplestack.helpers;

import android.os.Parcel;

import com.zhuinden.simplestack.ServiceBinder;

public abstract class TestKeyWithExplicitParent extends TestKeyWithScope implements HasParentServices {
    public TestKeyWithExplicitParent(String name) {
        super(name);
    }

    protected TestKeyWithExplicitParent(Parcel in) {
        super(in);
    }

    @Override
    public final void bindServices(ServiceBinder serviceBinder) {
        if(name.equals(serviceBinder.getScopeTag())) {
            bindOwnServices(serviceBinder);
        } else {
            bindParentServices(serviceBinder);
        }
    }

    protected abstract void bindParentServices(ServiceBinder serviceBinder);

    protected abstract void bindOwnServices(ServiceBinder serviceBinder);
}
