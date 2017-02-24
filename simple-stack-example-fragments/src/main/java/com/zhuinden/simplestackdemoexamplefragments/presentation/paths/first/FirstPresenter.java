package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.first;

import android.support.annotation.Nullable;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.StateBundle;
import com.zhuinden.simplestackdemoexamplefragments.presentation.paths.second.SecondKey;
import com.zhuinden.simplestackdemoexamplefragments.util.BasePresenter;

import javax.inject.Inject;

/**
 * Created by Owner on 2017. 01. 27..
 */

public class FirstPresenter
        extends BasePresenter<FirstFragment, FirstPresenter> {
    @Inject
    public FirstPresenter() {
    }

    @Inject
    Backstack backstack;

    public void goToSecondKey() {
        backstack.goTo(SecondKey.create());
    }

    @Override
    protected void onAttach(FirstFragment fragment) {
    }

    @Override
    protected void onDetach(FirstFragment fragment) {
    }

    @Override
    public StateBundle toBundle() {
        return new StateBundle();
    }

    @Override
    public void fromBundle(@Nullable StateBundle bundle) {

    }
}
