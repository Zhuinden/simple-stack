package com.zhuinden.stack_rx_example;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Owner on 2017. 02. 11..
 */

public class FirstView extends RelativeLayout {
    public FirstView(Context context) {
        super(context);
    }

    public FirstView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FirstView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public FirstView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @OnClick(R.id.first_button)
    public void clickFirstButton(View view) {
        BackstackService.getBackstack(view.getContext()).goTo(SecondKey.create());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }
}
