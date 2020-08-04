package com.zhuinden.navigationexamplecond.application;

import com.zhuinden.navigationexamplecond.screens.HomeKey;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.History;

import androidx.lifecycle.ViewModel;

class BackstackViewModel
        extends ViewModel {
    public final Backstack backstack = new Backstack();
    {
        backstack.setup(History.of(HomeKey.create()));
    }
}
