package com.zhuinden.simpleservicesexample.application;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.zhuinden.servicetree.ServiceTree;
import com.zhuinden.simplestack.navigator.StateKey;
import com.zhuinden.simplestack.navigator.ViewChangeHandler;
import com.zhuinden.simplestack.navigator.changehandlers.SegueViewChangeHandler;


/**
 * Created by Zhuinden on 2017.02.14..
 */

public abstract class Key
        implements StateKey, Parcelable {
    public abstract void bindServices(ServiceTree.Node node);

    @NonNull
    @Override
    public ViewChangeHandler viewChangeHandler() {
        return new SegueViewChangeHandler();
    }
}
