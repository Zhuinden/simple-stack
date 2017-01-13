package com.zhuinden.simplestackdemoexample;

import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import com.zhuinden.simplestackdemo.stack.Backstack;
import com.zhuinden.simplestackdemo.stack.StateChange;
import com.zhuinden.simplestackdemo.stack.StateChanger;
import com.zhuinden.simplestackdemoexample.demo.BackstackHolder;
import com.zhuinden.simplestackdemoexample.demo.FirstKey;
import com.zhuinden.simplestackdemoexample.demo.Key;
import com.zhuinden.simplestackdemoexample.R;
import com.zhuinden.simplestackdemoexample.demo.KeyHolder;

import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements StateChanger {
    @BindView(R.id.root)
    RelativeLayout root;

    Backstack backstack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ArrayList<Parcelable> keys;
        if(savedInstanceState != null) {
            keys = savedInstanceState.getParcelableArrayList("BACKSTACK");
        } else {
            keys = new ArrayList<>();
            keys.add(new FirstKey());
        }
        backstack = (Backstack)getLastCustomNonConfigurationInstance();
        if(backstack == null) {
            backstack = new Backstack(keys);
        }
        backstack.setStateChanger(this);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return backstack;
    }

    @Override
    public void onBackPressed() {
        if(!backstack.goBack()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Parcelable> history = new ArrayList<>();
        history.addAll(backstack.getHistory());
        outState.putParcelableArrayList("BACKSTACK", history);
    }

    @Override
    protected void onDestroy() {
        backstack.removeStateChanger();
        super.onDestroy();
    }

    @Override
    public void handleStateChange(StateChange stateChange, Callback completionCallback) {
        if(stateChange.topNewState().equals(stateChange.topPreviousState())) {
            // no-op
            completionCallback.stateChangeComplete();
            return;
        }
        root.removeAllViews();
        Key newKey = stateChange.topNewState();
        View view = LayoutInflater.from(this).inflate(newKey.layout(), root, false);
        BackstackHolder backstackHolder = (BackstackHolder)view;
        backstackHolder.setBackstack(backstack);
        KeyHolder keyHolder = (KeyHolder)view;
        keyHolder.setKey(newKey);
        root.addView(view);
        completionCallback.stateChangeComplete();
    }
}
