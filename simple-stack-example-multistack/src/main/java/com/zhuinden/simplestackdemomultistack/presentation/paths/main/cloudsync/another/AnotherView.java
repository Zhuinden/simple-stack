package com.zhuinden.simplestackdemomultistack.presentation.paths.main.cloudsync.another;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by Zhuinden on 2017.02.19..
 */

public class AnotherView
        extends RelativeLayout {
    public AnotherView(Context context) {
        super(context);
    }

    public AnotherView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnotherView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public AnotherView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
