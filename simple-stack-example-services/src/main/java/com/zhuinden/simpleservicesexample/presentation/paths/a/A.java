package com.zhuinden.simpleservicesexample.presentation.paths.a;

import com.google.auto.value.AutoValue;
import com.zhuinden.servicetree.ServiceTree;
import com.zhuinden.simpleservicesexample.R;
import com.zhuinden.simpleservicesexample.application.Key;
import com.zhuinden.simpleservicesexample.utils.MockService;


/**
 * Created by Zhuinden on 2017.02.14..
 */

@AutoValue
public abstract class A
        extends Key {
    @Override
    public int layout() {
        return R.layout.path_a;
    }

    public static A create() {
        return new AutoValue_A();
    }

    @Override
    public void bindServices(ServiceTree.Node.Binder binder) {
        binder.bindService("A", new MockService("A"));
    }
}
