package com.zhuinden.simplestackdemomultistack.presentation.paths.main.cloudsync.another;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.auto.value.AutoValue;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.NestedStack;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;
import com.zhuinden.simplestackdemomultistack.R;
import com.zhuinden.simplestackdemomultistack.application.Key;
import com.zhuinden.simplestackdemomultistack.application.MainActivity;
import com.zhuinden.simplestackdemomultistack.presentation.paths.main.chromecast.ChromeCastKey;
import com.zhuinden.simplestackdemomultistack.util.BackPressListener;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhuinden on 2017.02.19..
 */

public class AnotherView
        extends RelativeLayout
        implements StateChanger, BackPressListener {
    @AutoValue
    public abstract static class InternalKey
            extends Key {
        @Override
        public int layout() {
            return R.layout.path_another;
        }

        @Override
        public String stackIdentifier() {
            return MainActivity.StackType.CLOUDSYNC.name();
        }

        public static InternalKey create() {
            return new AutoValue_AnotherView_InternalKey();
        }
    }

    public AnotherView(Context context) {
        super(context);
    }

    public AnotherView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnotherView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public AnotherView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @BindView(R.id.another_text)
    TextView textView;

    @OnClick(R.id.another_back)
    public void backClicked() {
        nestedStack.goBack();
    }

    NestedStack nestedStack;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        nestedStack = Backstack.getNestedStack(getContext());
        nestedStack.initialize(InternalKey.create(), ChromeCastKey.create());
        nestedStack.setStateChanger(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        nestedStack.reattachStateChanger();
    }

    @Override
    protected void onDetachedFromWindow() {
        nestedStack.detachStateChanger();
        super.onDetachedFromWindow();
    }

    @Override
    public boolean onBackPressed() {
        return nestedStack.goBack();
    }

    @Override
    public void handleStateChange(StateChange stateChange, Callback completionCallback) {
        Log.i("ANOTHER",
                Arrays.toString(stateChange.getPreviousState().toArray()) + " :: " + Arrays.toString(stateChange.getNewState()
                        .toArray())); //
        textView.setText(stateChange.topNewState().toString());
        completionCallback.stateChangeComplete();
    }
}
