package com.zhuinden.simplestackdemo.demo;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import com.zhuinden.simplestackdemo.MainActivity;
import com.zhuinden.simplestackdemo.R;
import com.zhuinden.simplestackdemo.stack.Backstack;
import butterknife.ButterKnife;
import butterknife.OnClick;
/**
 * Created by Owner on 2017. 01. 12..
 */

public class FirstView extends RelativeLayout implements BackstackHolder {
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

    Backstack backstack;

    @OnClick(R.id.first_button)
    public void clickButton(View view) {
        backstack.goTo(new SecondKey());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override
    public void setBackstack(Backstack backstack) {
        this.backstack = backstack;
    }
}
