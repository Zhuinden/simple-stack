package com.zhuinden.simplestackdemonestedstack.application;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.zhuinden.simplestack.navigator.StateKey;
import com.zhuinden.simplestack.navigator.ViewChangeHandler;
import com.zhuinden.simplestack.navigator.changehandlers.SegueViewChangeHandler;
import com.zhuinden.simplestackdemonestedstack.util.PreserveTreeScopesStrategy;
import com.zhuinden.servicetree.ServiceTree;
import com.zhuinden.simplestack.BackstackManager;

import java.util.Collections;
import java.util.List;

/**
 * Created by Owner on 2017. 01. 12..
 */

public abstract class Key
        implements Parcelable, StateKey {
    public static final String NESTED_STACK = "NESTED_STACK";

    public abstract int layout();

    public abstract String stackIdentifier();

    public void bindServices(ServiceTree.Node node) {
        if(hasNestedStack()) {
            BackstackManager backstackManager = new BackstackManager();
            backstackManager.setStateClearStrategy(new PreserveTreeScopesStrategy(node.getTree()));
            backstackManager.setup(initialKeys());
            node.bindService(NESTED_STACK, backstackManager);
        }
    }

    protected List<?> initialKeys() {
        return Collections.emptyList();
    }

    public boolean hasNestedStack() {
        return false;
    }

    @NonNull
    @Override
    public ViewChangeHandler viewChangeHandler() {
        return new SegueViewChangeHandler();
    }
}
