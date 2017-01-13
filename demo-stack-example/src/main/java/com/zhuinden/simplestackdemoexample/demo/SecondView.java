package com.zhuinden.simplestackdemoexample.demo;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.zhuinden.simplestackdemo.stack.Backstack;

/**
 * Created by Owner on 2017. 01. 12..
 */

public class SecondView extends RelativeLayout implements BackstackHolder, KeyHolder {
    public SecondView(Context context) {
        super(context);
    }

    public SecondView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SecondView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public SecondView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    Backstack backstack;

    SecondKey secondKey;

    @Override
    public void setBackstack(Backstack backstack) {
        this.backstack = backstack;
    }

    @Override
    public void setKey(Key key) {
        this.secondKey = (SecondKey)key;
    }
}
