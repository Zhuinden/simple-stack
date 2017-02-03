package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.first;

import android.view.View;

import com.zhuinden.simplestackdemoexamplefragments.R;
import com.zhuinden.simplestackdemoexamplefragments.application.Injector;
import com.zhuinden.simplestackdemoexamplefragments.util.BaseFragment;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Owner on 2017. 01. 25..
 */
// UNSCOPED!
public class FirstFragment
        extends BaseFragment<FirstFragment, FirstPresenter> {
    @Inject
    FirstPresenter firstPresenter;

    private static final String TAG = "FirstFragment";

    @OnClick(R.id.first_button)
    public void clickButton(View view) {
        firstPresenter.goToSecondKey();
    }

    @Override
    public FirstPresenter getPresenter() {
        return firstPresenter;
    }

    @Override
    public FirstFragment getThis() {
        return this;
    }

    @Override
    protected Unbinder bindViews(View view) {
        return ButterKnife.bind(this, view);
    }

    @Override
    protected void injectSelf() {
        Injector.get().inject(this);
    }
}
