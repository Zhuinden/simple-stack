package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.first;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.zhuinden.simplestackdemoexamplemvp.R;
import com.zhuinden.simplestackdemoexamplemvp.application.CustomApplication;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.statebundle.StateBundle;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Owner on 2017. 01. 12..
 */

public class FirstView
        extends RelativeLayout
        implements Bundleable {
    private static final String TAG = "FirstView";

    public FirstView(Context context) {
        super(context);
        init(context);
    }

    public FirstView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FirstView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(21)
    public FirstView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    @Inject
    Backstack backstack;

    FirstKey firstKey;

    private void init(Context context) {
        if(!isInEditMode()) {
            CustomApplication.get().getComponent().inject(this);
            firstKey = Backstack.getKey(context);
        }
    }

    @Inject
    FirstPresenter firstPresenter;

    @OnClick(R.id.first_button)
    public void clickButton(View view) {
        firstPresenter.goToSecondKey();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        firstPresenter.attachView(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        firstPresenter.detachView(this);
        super.onDetachedFromWindow();
    }

    @Override
    public StateBundle toBundle() {
        StateBundle bundle = new StateBundle();
        bundle.putString("HELLO", "WORLD");
        return bundle;
    }

    @Override
    public void fromBundle(@Nullable StateBundle bundle) {
        if(bundle != null) {
            Log.i(TAG, bundle.getString("HELLO"));
        }
    }
}
