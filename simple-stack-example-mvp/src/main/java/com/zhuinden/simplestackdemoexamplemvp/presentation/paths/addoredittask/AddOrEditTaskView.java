package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.addoredittask;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by Owner on 2017. 01. 26..
 */

public class AddOrEditTaskView
        extends ScrollView {
    public AddOrEditTaskView(Context context) {
        super(context);
    }

    public AddOrEditTaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AddOrEditTaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public AddOrEditTaskView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
