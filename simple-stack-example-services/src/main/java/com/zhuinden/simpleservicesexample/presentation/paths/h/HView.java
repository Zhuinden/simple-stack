package com.zhuinden.simpleservicesexample.presentation.paths.h;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.zhuinden.simpleservicesexample.R;
import com.zhuinden.simpleservicesexample.presentation.paths.i.I;
import com.zhuinden.simpleservicesexample.utils.StackService;
import com.zhuinden.simplestack.Backstack;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Owner on 2017. 02. 16..
 */

public class HView
        extends RelativeLayout {
    public HView(Context context) {
        super(context);
    }

    public HView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public HView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @OnClick(R.id.h_button)
    public void click(View view) {
        StackService.get(getContext()).goTo(I.create(Backstack.getKey(getContext())));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }
}
