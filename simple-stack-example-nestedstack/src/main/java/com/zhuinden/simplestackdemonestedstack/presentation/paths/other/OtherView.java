package com.zhuinden.simplestackdemonestedstack.presentation.paths.other;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.zhuinden.simplestack.Backstack;

import butterknife.ButterKnife;

/**
 * Created by Owner on 2017. 02. 27..
 */

public class OtherView
        extends RelativeLayout {
    public OtherView(Context context) {
        super(context);
        init(context);
    }

    public OtherView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public OtherView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(21)
    public OtherView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    OtherKey otherKey;

    private void init(Context context) {
        if(!isInEditMode()) {
            otherKey = Backstack.getKey(context);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }
}
