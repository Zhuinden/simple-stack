package com.zhuinden.simplestackdemoexample;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.zhuinden.simplestackdemo.stack.Backstack;
import com.zhuinden.simplestackdemo.stack.HistoryBuilder;
import com.zhuinden.simplestackdemo.stack.StateChange;
import com.zhuinden.simplestackdemo.stack.StateChanger;
import com.zhuinden.simplestackdemoexample.demo.FirstKey;
import com.zhuinden.simplestackdemoexample.demo.Key;
import com.zhuinden.simplestackdemoexample.demo.KeyContextWrapper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements StateChanger {
    public static final String BACKSTACK = "BACKSTACK";

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
            keys = savedInstanceState.getParcelableArrayList(BACKSTACK);
        } else {
            keys = HistoryBuilder.single(new FirstKey());
        }
        backstack = (Backstack)getLastCustomNonConfigurationInstance();
        if(backstack == null) {
            backstack = new Backstack(keys);
        }
        backstack.setStateChanger(this, Backstack.INITIALIZE);
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
        outState.putParcelableArrayList(BACKSTACK, HistoryBuilder.from(backstack.getHistory()).build());
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(!backstack.hasStateChanger()) {
            backstack.setStateChanger(this, Backstack.REATTACH);
        }
    }

    @Override
    protected void onPause() {
        if(backstack.hasStateChanger()) {
            backstack.removeStateChanger();
        }
        super.onPause();
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
        Context newContext = new KeyContextWrapper(this, newKey);
        View view = LayoutInflater.from(newContext).inflate(newKey.layout(), root, false);
        root.addView(view);
        completionCallback.stateChangeComplete();
    }

    @Override
    public Object getSystemService(String name) {
        if(BACKSTACK.equals(name)) {
            return backstack;
        }
        return super.getSystemService(name);
    }
}
