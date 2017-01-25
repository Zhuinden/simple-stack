package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.second;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.squareup.coordinators.Coordinator;
import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.simplestackdemoexamplemvp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import butterknife.Unbinder;

/**
 * Created by Owner on 2017. 01. 25..
 */

public class SecondCoordinator extends Coordinator implements Bundleable {
    private static final String TAG = "SecondCoordinator";

    String state;

    @OnTextChanged(R.id.second_edittext)
    public void textChanged(Editable editable) {
        this.state = editable.toString();
    }

    @BindView(R.id.second_edittext)
    EditText editText;

    Unbinder unbinder;

    @Override
    public void attach(View view) {
        Log.i(TAG, "Attached [" + view + "]");
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
