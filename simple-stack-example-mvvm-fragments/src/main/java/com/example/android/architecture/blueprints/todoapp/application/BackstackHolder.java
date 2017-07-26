package com.example.android.architecture.blueprints.todoapp.application;

import com.zhuinden.simplestack.Backstack;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Owner on 2017. 07. 26..
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
