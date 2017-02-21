package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.second;

import android.support.annotation.Nullable;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.simplestack.StateBundle;
import com.zhuinden.simplestackdemoexamplemvp.R;
import com.zhuinden.simplestackdemoexamplemvp.util.BaseCoordinator;

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
public class SecondCoordinator
        extends BaseCoordinator<SecondCoordinator, SecondPresenter>
        implements Bundleable {
    @Inject
    public SecondCoordinator() {
    }

    @Inject
    SecondPresenter secondPresenter;

    private static final String TAG = "SecondCoordinator";

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
    public SecondCoordinator getThis() {
        return this;
    }

    @Override
    protected Unbinder bindViews(View view) {
        return ButterKnife.bind(this, view);
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
}
