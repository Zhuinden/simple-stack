package com.zhuinden.simplestackdemoexample.demo;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestackdemoexample.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
/**
 * Created by Owner on 2017. 01. 12..
 */

public class FirstView
        extends RelativeLayout {
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

    Backstack backstack;

    FirstKey firstKey;

    private void init(Context context) {
        if(!isInEditMode()) {
            backstack = Backstack.get(context);
            firstKey = Backstack.getKey(context);
        }
    }

    @OnClick(R.id.first_button)
    public void clickButton(View view) {
        backstack.goTo(new SecondKey());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }
}
