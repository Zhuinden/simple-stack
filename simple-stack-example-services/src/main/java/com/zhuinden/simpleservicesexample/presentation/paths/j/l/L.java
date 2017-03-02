package com.zhuinden.simpleservicesexample.presentation.paths.j.l;

import com.google.auto.value.AutoValue;
import com.zhuinden.servicetree.ServiceTree;
import com.zhuinden.simpleservicesexample.application.Key;
import com.zhuinden.simpleservicesexample.utils.MockService;


/**
 * Created by Owner on 2017. 02. 17..
 */
@AutoValue
public abstract class L
        extends Key {
    @Override
    public int layout() {
        return 0;
    }

    public static L create() {
        return new AutoValue_L();
    }

    @Override
    public void bindServices(ServiceTree.Node.Binder binder) {
        binder.bindService("L", new MockService("L"));
    }
}
