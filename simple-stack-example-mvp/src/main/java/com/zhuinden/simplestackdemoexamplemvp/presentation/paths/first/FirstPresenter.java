package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.first;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.second.SecondKey;
import com.zhuinden.simplestackdemoexamplemvp.util.BasePresenter;

import javax.inject.Inject;

/**
 * Created by Owner on 2017. 01. 27..
 */

public class FirstPresenter
        extends BasePresenter<FirstCoordinator, FirstPresenter> {
    @Inject
    public FirstPresenter() {
    }

    @Inject
    Backstack backstack;

    @Override
    protected void onAttach(FirstCoordinator coordinator) {
    }

    @Override
    protected void onDetach(FirstCoordinator coordinator) {
    }

    public void goToSecondKey() {
        backstack.goTo(SecondKey.create());
    }
}
