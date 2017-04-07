package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.second;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.zhuinden.simplestackdemoexamplemvp.R;
import com.zhuinden.simplestackdemoexamplemvp.application.CustomApplication;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.statebundle.StateBundle;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Created by Owner on 2017. 01. 12..
 */

public class SecondView
        extends RelativeLayout
        implements Bundleable {
    public SecondView(Context context) {
        super(context);
        init(context);
    }

    public SecondView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SecondView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(21)
    public SecondView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        if(!isInEditMode()) {
            CustomApplication.get().getComponent().inject(this);
            secondKey = Backstack.getKey(context);
        }
    }

    @Inject
    Backstack backstack;

    SecondKey secondKey;


    @Inject
    SecondPresenter secondPresenter;

    @OnTextChanged(R.id.second_edittext)
    public void textChanged(Editable editable) {
        secondPresenter.updateState(editable.toString());
    }

    @BindView(R.id.second_edittext)
    EditText editText;

    @OnClick(R.id.second_go_to_todos)
    public void goToTodos() {
        secondPresenter.goToTodos();
    }

    @Override
    public StateBundle toBundle() {
        return secondPresenter.toBundle();
    }

    @Override
    public void fromBundle(@Nullable StateBundle bundle) {
        if(bundle != null) {
            secondPresenter.fromBundle(bundle);
        }
    }

    public void setStateText(String state) {
        if(!editText.getText().toString().equals(state)) {
            editText.setText(state);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        secondPresenter.attachView(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        secondPresenter.detachView(this);
        super.onDetachedFromWindow();
    }
}
