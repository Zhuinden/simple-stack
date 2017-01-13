package com.zhuinden.simplestackdemoexample.demo;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.zhuinden.simplestackdemo.stack.Backstack;
import com.zhuinden.simplestackdemoexample.MainActivity;

/**
 * Created by Owner on 2017. 01. 12..
 */

public class SecondView
        extends RelativeLayout {
    public SecondView(Context context) {
        super(context);
        init(context);
    }

    public SecondView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SecondView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(21)
    public SecondView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        if(!isInEditMode()) {
            // noinspection ResourceType
            backstack = (Backstack) context.getSystemService(MainActivity.BACKSTACK);
            secondKey = KeyContextWrapper.getKey(context);
        }
    }

    Backstack backstack;

    SecondKey secondKey;
}
