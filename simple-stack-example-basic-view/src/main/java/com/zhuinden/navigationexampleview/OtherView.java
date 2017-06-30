package com.zhuinden.navigationexampleview;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;

/**
 * Created by Owner on 2017. 06. 29..
 */

public class OtherView extends RelativeLayout {
    public OtherView(Context context) {
        super(context);
    }

    public OtherView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OtherView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public OtherView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }
}
