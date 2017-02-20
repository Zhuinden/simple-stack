package com.zhuinden.simplestackdemomultistack.presentation.paths.main.chromecast;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.zhuinden.simplestack.Backstack;

/**
 * Created by Owner on 2017. 01. 12..
 */

public class ChromeCastView
        extends RelativeLayout {
    public ChromeCastView(Context context) {
        super(context);
        init(context);
    }

    public ChromeCastView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChromeCastView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(21)
    public ChromeCastView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        if(!isInEditMode()) {
            chromeCastKey = Backstack.getKey(context);
        }
    }

    ChromeCastKey chromeCastKey;
}
