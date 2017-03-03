package com.zhuinden.simplestackdemonestedstack.presentation.paths.main.cloudsync.another;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.BackstackManager;
import com.zhuinden.simplestack.KeyContextWrapper;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;
import com.zhuinden.simplestackdemonestedstack.R;
import com.zhuinden.simplestackdemonestedstack.application.Key;
import com.zhuinden.simplestackdemonestedstack.application.MainActivity;
import com.zhuinden.simplestackdemonestedstack.util.BackPressListener;
import com.zhuinden.simplestackdemonestedstack.util.NestSupportServiceManager;
import com.zhuinden.simplestackdemonestedstack.util.ServiceLocator;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Zhuinden on 2017.02.19..
 */

public class AnotherView
        extends RelativeLayout
        implements StateChanger, BackPressListener {
    public AnotherView(Context context) {
        super(context);
        init(context);
    }

    public AnotherView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AnotherView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(21)
    public AnotherView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        if(!isInEditMode()) {
            anotherKey = Backstack.getKey(context);
        }
    }

    AnotherKey anotherKey;

    @BindView(R.id.another_nested_container)
    FrameLayout nestedContainer;

    BackstackManager backstackManager;

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        backstackManager = ServiceLocator.getService(getContext(), Key.NESTED_STACK);
        backstackManager.setStateChanger(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        backstackManager.reattachStateChanger();
    }

    @Override
    protected void onDetachedFromWindow() {
        backstackManager.detachStateChanger();
        super.onDetachedFromWindow();
    }

    @Override
    public boolean onBackPressed() {
        if(nestedContainer.getChildAt(0) != null && nestedContainer.getChildAt(0) instanceof BackPressListener) {
            boolean handled = ((BackPressListener) nestedContainer.getChildAt(0)).onBackPressed();
            if(handled) {
                return true;
            }
        }
        return backstackManager.getBackstack().goBack();
    }

    @Override
    public void handleStateChange(StateChange stateChange, Callback completionCallback) {
        NestSupportServiceManager.get(getContext()).setupServices(stateChange);

        if(stateChange.topNewState().equals(stateChange.topPreviousState())) {
            completionCallback.stateChangeComplete();
            return;
        }
        Key newKey = stateChange.topNewState();
        if(nestedContainer.getChildAt(0) != null) {
            backstackManager.persistViewToState(nestedContainer.getChildAt(0));
            nestedContainer.removeAllViews();
        }
        View newView = LayoutInflater.from(new KeyContextWrapper(MainActivity.get(getContext()), newKey))
                .inflate(newKey.layout(), this, false);
        backstackManager.restoreViewFromState(newView);
        nestedContainer.addView(newView);
        completionCallback.stateChangeComplete();
    }
}
