package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.second;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.squareup.coordinators.Coordinator;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.simplestackdemoexamplemvp.R;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.tasks.TasksKey;

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
public class SecondCoordinator extends Coordinator implements Bundleable {
    @Inject
    public SecondCoordinator() {
    }

    private static final String TAG = "SecondCoordinator";

    String state;

    @OnTextChanged(R.id.second_edittext)
    public void textChanged(Editable editable) {
        this.state = editable.toString();
    }

    @BindView(R.id.second_edittext)
    EditText editText;

    @OnClick(R.id.second_go_to_todos)
    public void goToTodos() {
        backstack.goTo(TasksKey.create());
    }

    Unbinder unbinder;

    Backstack backstack;

    @Override
    public void attach(View view) {
        Log.i(TAG, "Attached [" + view + "]");
        backstack = Backstack.get(view.getContext());
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void detach(View view) {
        Log.i(TAG, "Detached [" + view + "]");
        unbinder.unbind();
    }

    @Override
    public Bundle toBundle() {
        Log.i(TAG, "To bundle");
        Bundle bundle = new Bundle();
        bundle.putString("state", state);
        return bundle;
    }

    @Override
    public void fromBundle(@Nullable Bundle bundle) {
        Log.i(TAG, "From bundle");
        if(bundle != null) {
            setState(bundle.getString("state"));
        }
    }

    public void setState(String state) {
        this.state = state;
        editText.setText(state);
    }
}
