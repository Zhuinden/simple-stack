package com.zhuinden.simplestackdemonestedstack.presentation.paths.main.cloudsync.another.internal;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.NestedStack;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;
import com.zhuinden.simplestackdemonestedstack.R;
import com.zhuinden.simplestackdemonestedstack.application.MainActivity;
import com.zhuinden.simplestackdemonestedstack.presentation.paths.main.chromecast.ChromeCastKey;
import com.zhuinden.simplestackdemonestedstack.presentation.paths.main.list.ListKey;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhuinden on 2017.02.25..
 */

public class InternalView extends RelativeLayout implements StateChanger {
    public InternalView(Context context) {
        super(context);
    }

    public InternalView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InternalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public InternalView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    NestedStack nestedStack;

    @BindView(R.id.another_text)
    TextView textView;

    @OnClick(R.id.another_back)
    public void backClicked() {
        if(!nestedStack.goBack()) {
            MainActivity.get(getContext()).onBackPressed();
        }
    }

    @OnClick(R.id.another_forward)
    public void forwardClicked() {
        nestedStack.goTo(ChromeCastKey.create());
    }


    @OnClick(R.id.internal_button)
    public void click() {
        Toast.makeText(getContext(), "Hello!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        nestedStack = Backstack.getNestedStack(getContext());
        nestedStack.initialize(ListKey.create());
        nestedStack.setStateChanger(this);
    }

    @Override
    public void handleStateChange(StateChange stateChange, Callback completionCallback) {
        completionCallback.stateChangeComplete();
    }
}
