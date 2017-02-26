package com.zhuinden.simplestackdemonestedstack.presentation.paths.main.cloudsync;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.simplestack.NestedStack;
import com.zhuinden.simplestack.StateBundle;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;
import com.zhuinden.simplestackdemonestedstack.R;
import com.zhuinden.simplestackdemonestedstack.application.Key;
import com.zhuinden.simplestackdemonestedstack.presentation.paths.main.cloudsync.another.AnotherKey;

import butterknife.BindView;
import butterknife.ButterKnife;
/**
 * Created by Owner on 2017. 01. 12..
 */

public class CloudSyncView
        extends RelativeLayout
        implements Bundleable, StateChanger {
    private static final String TAG = "FirstView";

    public CloudSyncView(Context context) {
        super(context);
        init(context);
    }

    public CloudSyncView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CloudSyncView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(21)
    public CloudSyncView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    CloudSyncKey cloudSyncKey;

    @BindView(R.id.cloudsync_nested_container)
    FrameLayout nestedContainer;

    NestedStack nestedStack;

    private void init(Context context) {
        if(!isInEditMode()) {
            cloudSyncKey = Backstack.getKey(context);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        nestedStack = Backstack.getNestedStack(getContext());
        nestedStack.initialize(AnotherKey.create());
        nestedStack.setStateChanger(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        nestedStack.reattachStateChanger();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        nestedStack.detachStateChanger();
    }

    @Override
    public StateBundle toBundle() {
        StateBundle bundle = new StateBundle();
        bundle.putString("HELLO", "WORLD");
        StateBundle innerBundle = new StateBundle();
        innerBundle.putString("KAPPA", "KAPPA");
        bundle.putBundle("GOOMBA", innerBundle);
        return bundle;
    }

    @Override
    public void fromBundle(@Nullable StateBundle bundle) {
        if(bundle != null) {
            Log.i(TAG, bundle.getString("HELLO"));
            Log.i(TAG, bundle.getBundle("GOOMBA") == null ? null : bundle.getBundle("GOOMBA").getString("KAPPA"));
        }
    }

    @Override
    public void handleStateChange(StateChange stateChange, Callback completionCallback) {
        if(stateChange.topNewState().equals(stateChange.topPreviousState())) {
            completionCallback.stateChangeComplete();
            return;
        }
        Key newKey = stateChange.topNewState();
        if(nestedContainer.getChildAt(0) != null) {
            nestedStack.persistViewToState(nestedContainer.getChildAt(0));
            nestedContainer.removeAllViews();
        }
        View newView = LayoutInflater.from(nestedStack.createContext(getContext(), newKey)).inflate(newKey.layout(), this, false);
        nestedStack.restoreViewFromState(newView);
        nestedContainer.addView(newView);
        completionCallback.stateChangeComplete();
    }
}
