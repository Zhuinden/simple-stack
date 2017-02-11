package com.zhuinden.stack_rx_example;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;

/**
 * Created by Owner on 2017. 02. 11..
 */

public class SecondView extends RelativeLayout {
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

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }
}
