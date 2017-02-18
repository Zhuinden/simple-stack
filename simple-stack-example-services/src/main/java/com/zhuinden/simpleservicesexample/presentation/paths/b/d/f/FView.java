package com.zhuinden.simpleservicesexample.presentation.paths.b.d.f;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.zhuinden.simpleservicesexample.utils.Preconditions;
import com.zhuinden.simpleservicesexample.utils.ServiceLocator;

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
        Preconditions.checkNotNull(ServiceLocator.getService(getContext(), "A"), "Service should not be null");
        Preconditions.checkNotNull(ServiceLocator.getService(getContext(), "B"), "Service should not be null");
        Preconditions.checkNotNull(ServiceLocator.getService(getContext(), "D"), "Service should not be null");
        Preconditions.checkNotNull(ServiceLocator.getService(getContext(), "F"), "Service should not be null");
    }
}
