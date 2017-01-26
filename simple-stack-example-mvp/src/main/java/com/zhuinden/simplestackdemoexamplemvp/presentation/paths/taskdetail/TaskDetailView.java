package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.taskdetail;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by Zhuinden on 2017.01.26..
 */

public class TaskDetailView
        extends RelativeLayout {
    public TaskDetailView(Context context) {
        super(context);
    }

    public TaskDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TaskDetailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public TaskDetailView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
