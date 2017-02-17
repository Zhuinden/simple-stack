package com.zhuinden.simpleservicesexample.presentation.paths.k;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by Owner on 2017. 02. 17..
 */

public class KView
        extends RelativeLayout {
    public KView(Context context) {
        super(context);
    }

    public KView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public KView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
