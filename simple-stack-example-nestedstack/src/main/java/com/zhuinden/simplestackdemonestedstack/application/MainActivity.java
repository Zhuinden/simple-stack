package com.zhuinden.simplestackdemonestedstack.application;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.BackstackDelegate;
import com.zhuinden.simplestack.HistoryBuilder;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;
import com.zhuinden.simplestackdemonestedstack.R;
import com.zhuinden.simplestackdemonestedstack.presentation.paths.main.MainKey;
import com.zhuinden.simplestackdemonestedstack.util.BackPressListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity
        extends AppCompatActivity
        implements StateChanger {
    private static final String TAG = "MainActivity";

    public static MainActivity get(Context context) {
        //noinspection ResourceType
        return (MainActivity)context.getSystemService(TAG);
    }

    @BindView(R.id.view_root)
    FrameLayout root;

    BackstackDelegate backstackDelegate;
    Backstack backstack;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        backstackDelegate = BackstackDelegate.create();
        backstackDelegate.onCreate(savedInstanceState, getLastCustomNonConfigurationInstance(), HistoryBuilder.single(MainKey.create()));
        backstack = backstackDelegate.getBackstack();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        backstackDelegate.setStateChanger(this);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return backstackDelegate.onRetainCustomNonConfigurationInstance();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        backstackDelegate.onPostResume();
    }

    @Override
    public void onBackPressed() {
        if(root.getChildAt(0) != null && root.getChildAt(0) instanceof BackPressListener) {
            boolean handled = ((BackPressListener) root.getChildAt(0)).onBackPressed();
            if(handled) {
                return;
            }
        }
        if(!backstackDelegate.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        backstackDelegate.onPause();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        backstackDelegate.persistViewToState(root.getChildAt(0));
        backstackDelegate.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        backstackDelegate.onDestroy();
        super.onDestroy();
    }

    @Override
    public Object getSystemService(String name) {
        if(MainActivity.TAG.equals(name)) {
            return this;
        }
        return super.getSystemService(name);
    }

    @Override
    public void handleStateChange(StateChange stateChange, Callback completionCallback) {
        if(stateChange.topNewState().equals(stateChange.topPreviousState())) {
            completionCallback.stateChangeComplete();
            return;
        }
        backstackDelegate.persistViewToState(root.getChildAt(0));
        root.removeAllViews();
        Key newKey = stateChange.topNewState();
        View newView = LayoutInflater.from(backstackDelegate.createContext(this, newKey)).inflate(newKey.layout(), root, false);
        backstackDelegate.restoreViewFromState(newView);
        root.addView(newView);
        completionCallback.stateChangeComplete();
    }
}