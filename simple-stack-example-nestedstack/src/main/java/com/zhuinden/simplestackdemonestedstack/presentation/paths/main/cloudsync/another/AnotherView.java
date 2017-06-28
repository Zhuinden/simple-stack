package com.zhuinden.simplestackdemonestedstack.presentation.paths.main.cloudsync.another;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.BackstackManager;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;
import com.zhuinden.simplestack.navigator.DefaultStateChanger;
import com.zhuinden.simplestackdemonestedstack.R;
import com.zhuinden.simplestackdemonestedstack.application.Key;
import com.zhuinden.simplestackdemonestedstack.util.BackstackManagerPersistenceStrategy;
import com.zhuinden.simplestackdemonestedstack.util.NestSupportServiceManager;
import com.zhuinden.simplestackdemonestedstack.util.ServiceLocator;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Zhuinden on 2017.02.19..
 */

public class AnotherView
        extends RelativeLayout
        implements StateChanger {
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
        backstackManager.setStateChanger(DefaultStateChanger.configure()
                .setExternalStateChanger(this)
                .setStatePersistenceStrategy(new BackstackManagerPersistenceStrategy(backstackManager))
                .create(getContext(), nestedContainer));
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
    public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
        NestSupportServiceManager.get(getContext()).setupServices(stateChange);
        completionCallback.stateChangeComplete();
    }
}
