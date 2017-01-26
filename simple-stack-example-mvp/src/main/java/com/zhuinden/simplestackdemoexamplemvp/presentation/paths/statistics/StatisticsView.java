package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.statistics;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by Zhuinden on 2017.01.26..
 */

public class StatisticsView
        extends LinearLayout {
    public StatisticsView(Context context) {
        super(context);
    }

    public StatisticsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StatisticsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public StatisticsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
