package com.zhuinden.simplestackdemonestedstack.presentation.paths.main.chromecast;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.simplestackdemonestedstack.R;
import com.zhuinden.statebundle.StateBundle;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Owner on 2017. 01. 12..
 */

public class ChromeCastView
        extends RelativeLayout
        implements Bundleable {
    public ChromeCastView(Context context) {
        super(context);
        init(context);
    }

    public ChromeCastView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChromeCastView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(21)
    public ChromeCastView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        if(!isInEditMode()) {
            chromeCastKey = Backstack.getKey(context);
        }
    }

    @BindView(R.id.second_edittext)
    EditText editText;

    ChromeCastKey chromeCastKey;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @NonNull
    @Override
    public StateBundle toBundle() {
        StateBundle stateBundle = new StateBundle();
        stateBundle.putString("text", editText.getText().toString());
        return stateBundle;
    }

    @Override
    public void fromBundle(@Nullable StateBundle bundle) {
        if(bundle != null) {
            editText.setText(bundle.getString("text"));
        }
    }
}
