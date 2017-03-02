package com.zhuinden.simpleservicesexample.presentation.paths.b.c;

import com.google.auto.value.AutoValue;
import com.zhuinden.servicetree.ServiceTree;
import com.zhuinden.simpleservicesexample.R;
import com.zhuinden.simpleservicesexample.application.Key;


/**
 * Created by Zhuinden on 2017.02.14..
 */

@AutoValue
public abstract class C
        extends Key {
    @Override
    public int layout() {
        return R.layout.path_c;
    }

    public static C create() {
        return new AutoValue_C();
    }

    @Override
    public void bindServices(ServiceTree.Node.Binder binder) {
        binder.bindService("C", "C");
    }
}
