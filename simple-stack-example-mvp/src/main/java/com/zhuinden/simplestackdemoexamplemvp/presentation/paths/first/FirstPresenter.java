package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.first;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.second.SecondKey;
import com.zhuinden.simplestackdemoexamplemvp.util.BasePresenter;

import javax.inject.Inject;

/**
 * Created by Owner on 2017. 01. 27..
 */

public class FirstPresenter
        extends BasePresenter<FirstView, FirstPresenter> {
    @Inject
    public FirstPresenter() {
    }

    @Inject
    Backstack backstack;

    @Override
    protected void onAttach(FirstView view) {
    }

    @Override
    protected void onDetach(FirstView view) {
    }

    public void goToSecondKey() {
        backstack.goTo(SecondKey.create());
    }
}
