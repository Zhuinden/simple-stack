package com.zhuinden.simpleservicesexample.presentation.paths.b.c;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.zhuinden.simpleservicesexample.utils.Preconditions;
import com.zhuinden.simpleservicesexample.utils.ServiceLocator;

/**
 * Created by Zhuinden on 2017.02.14..
 */

public class CView
        extends RelativeLayout {
    public CView(Context context) {
        super(context);
    }

    public CView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public CView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Preconditions.checkNotNull(ServiceLocator.getService(getContext(), "A"), "Service should not be null");
        Preconditions.checkNotNull(ServiceLocator.getService(getContext(), "B"), "Service should not be null");
        Preconditions.checkNotNull(ServiceLocator.getService(getContext(), "C"), "Service should not be null");
    }
}
