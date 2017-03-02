package com.zhuinden.simplestackdemonestedstack.application;

import android.content.Context;
import android.os.Parcelable;

import com.zhuinden.servicetree.ServiceTree;
import com.zhuinden.simplestack.BackstackDelegate;
import com.zhuinden.simplestack.BackstackManager;
import com.zhuinden.simplestackdemonestedstack.util.ServiceLocator;

import java.util.Collections;
import java.util.List;

/**
 * Created by Owner on 2017. 01. 12..
 */

public abstract class Key
        implements Parcelable {
    public static final String NESTED_STACK = "NESTED_STACK";

    public abstract int layout();

    public final BackstackDelegate selectDelegate(Context context) {
        return ServiceLocator.getService(context, stackIdentifier());
    }

    public abstract String stackIdentifier();

    public void bindServices(ServiceTree.Node.Binder binder) {
        if(hasNestedStack()) {
            BackstackManager backstackManager = createBackstackManager();
            backstackManager.setup(initialKeys());
            binder.bindService(NESTED_STACK, backstackManager);
        }
    }

    protected List<?> initialKeys() {
        return Collections.emptyList();
    }

    public boolean hasNestedStack() {
        return false;
    }

    protected BackstackManager createBackstackManager() {
        return new BackstackManager();
    }
}
