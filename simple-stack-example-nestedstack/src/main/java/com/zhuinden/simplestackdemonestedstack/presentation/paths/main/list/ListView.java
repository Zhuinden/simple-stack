package com.zhuinden.simplestackdemonestedstack.presentation.paths.main.list;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.zhuinden.simplestack.Backstack;

/**
 * Created by Zhuinden on 2017.02.19..
 */

public class ListView
        extends RelativeLayout {
    public ListView(Context context) {
        super(context);
        init(context);
    }

    public ListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(21)
    public ListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    ListKey listKey;

    private void init(Context context) {
        if(!isInEditMode()) {
            listKey = Backstack.getKey(context);
        }
    }
}
