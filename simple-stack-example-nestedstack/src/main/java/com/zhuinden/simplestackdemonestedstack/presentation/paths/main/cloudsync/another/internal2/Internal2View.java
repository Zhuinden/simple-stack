package com.zhuinden.simplestackdemonestedstack.presentation.paths.main.cloudsync.another.internal2;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.NestedStack;
import com.zhuinden.simplestackdemonestedstack.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhuinden on 2017.02.26..
 */

public class Internal2View
        extends RelativeLayout {
    public Internal2View(Context context) {
        super(context);
        init(context);
    }

    public Internal2View(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Internal2View(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(21)
    public Internal2View(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    Internal2Key internal2Key;

    private void init(Context context) {
        if(!isInEditMode()) {
            internal2Key = Backstack.getKey(context);
        }
    }

    NestedStack nestedStack;

    @OnClick(R.id.internal2_button)
    public void click() {
        nestedStack.getParent().goBack();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        nestedStack = Backstack.getNestedStack(getContext());
    }
}
