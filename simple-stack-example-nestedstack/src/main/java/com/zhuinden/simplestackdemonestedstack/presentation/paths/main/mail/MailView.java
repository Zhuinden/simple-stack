package com.zhuinden.simplestackdemonestedstack.presentation.paths.main.mail;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by Zhuinden on 2017.02.19..
 */

public class MailView
        extends RelativeLayout {
    public MailView(Context context) {
        super(context);
    }

    public MailView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public MailView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
