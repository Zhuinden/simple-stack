package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.second;

import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import com.zhuinden.simplestackdemoexamplefragments.R;
import com.zhuinden.simplestackdemoexamplefragments.application.Injector;
import com.zhuinden.simplestackdemoexamplefragments.util.BaseFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;

/**
 * Created by Owner on 2017. 01. 25..
 */
// UNSCOPED!
public class SecondFragment
        extends BaseFragment<SecondFragment, SecondPresenter> {
    public SecondFragment() {
    }

    @Inject
    SecondPresenter secondPresenter;

    private static final String TAG = "SecondFragment";

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
    public SecondPresenter getPresenter() {
        return secondPresenter;
    }

    @Override
    public SecondFragment getThis() {
        return this;
    }

    @Override
    protected Unbinder bindViews(View view) {
        return ButterKnife.bind(this, view);
    }

    @Override
    protected void injectSelf() {
        Injector.get().inject(this);
    }

    public void setStateText(String state) {
        if(!editText.getText().toString().equals(state)) {
            editText.setText(state);
        }
    }
}
