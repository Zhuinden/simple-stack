package com.zhuinden.simplestackdemoexamplefragments.util;

import com.zhuinden.simplestack.Backstack;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Owner on 2017. 01. 27..
 */
@Singleton
public class BackstackHolder {
    private Backstack backstack;

    @Inject
    public BackstackHolder() {
    }

    public Backstack getBackstack() {
        return this.backstack;
    }

    public void setBackstack(Backstack backstack) {
        this.backstack = backstack;
    }
}
