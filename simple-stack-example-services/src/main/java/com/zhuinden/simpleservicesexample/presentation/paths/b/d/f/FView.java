package com.zhuinden.simpleservicesexample.presentation.paths.b.d.f;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.zhuinden.simpleservicesexample.application.MainActivity;
import com.zhuinden.simpleservicesexample.utils.Preconditions;
import com.zhuinden.simplestack.Backstack;

/**
 * Created by Zhuinden on 2017.02.14..
 */

public class FView
        extends RelativeLayout {
    public FView(Context context) {
        super(context);
    }

    public FView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public FView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
//        Preconditions.checkNotNull(MainActivity.getServices(getContext()).findServices(Backstack.getKey(getContext())).getService("A"),
//                "Service should not be null");
//        Preconditions.checkNotNull(MainActivity.getServices(getContext()).findServices(Backstack.getKey(getContext())).getService("B"),
//                "Service should not be null");
//        Preconditions.checkNotNull(MainActivity.getServices(getContext()).findServices(Backstack.getKey(getContext())).getService("D"),
//                "Service should not be null");
//        Preconditions.checkNotNull(MainActivity.getServices(getContext()).findServices(Backstack.getKey(getContext())).getService("F"),
//                "Service should not be null");
    }
}
