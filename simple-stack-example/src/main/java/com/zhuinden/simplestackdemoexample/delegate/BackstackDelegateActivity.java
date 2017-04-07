package com.zhuinden.simplestackdemoexample.delegate;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.zhuinden.simplestack.BackstackDelegate;
import com.zhuinden.simplestack.HistoryBuilder;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;
import com.zhuinden.simplestack.navigator.DefaultStateChanger;
import com.zhuinden.simplestackdemoexample.R;
import com.zhuinden.simplestackdemoexample.common.BackstackService;
import com.zhuinden.simplestackdemoexample.common.FirstKey;
import com.zhuinden.simplestackdemoexample.common.Key;


import butterknife.BindView;
import butterknife.ButterKnife;

public class BackstackDelegateActivity
        extends AppCompatActivity
        implements StateChanger {
    @BindView(R.id.root)
    RelativeLayout root;

    BackstackDelegate backstackDelegate;
    DefaultStateChanger defaultStateChanger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        backstackDelegate = new BackstackDelegate(null);
        backstackDelegate.onCreate(savedInstanceState, //
                getLastCustomNonConfigurationInstance(), //
                HistoryBuilder.single(new FirstKey()));
        defaultStateChanger = DefaultStateChanger.configure()
                .setStatePersistenceStrategy(new DefaultStateChanger.StatePersistenceStrategy() {
                    @Override
                    public void persistViewToState(@NonNull Object previousKey, @NonNull View previousView) {
                        backstackDelegate.persistViewToState(previousView);
                    }

                    @Override
                    public void restoreViewFromState(@NonNull Object newKey, @NonNull View newView) {
                        backstackDelegate.restoreViewFromState(newView);
                    }
                })
                .create(this, root);
        backstackDelegate.setStateChanger(defaultStateChanger);
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
        if(name.equals(BackstackService.TAG)) {
            return backstackDelegate.getBackstack();
        }
        return super.getSystemService(name);
    }

    @Override
    public void handleStateChange(StateChange stateChange, Callback completionCallback) {
        if(stateChange.topNewState().equals(stateChange.topPreviousState())) {
            // no-op
            completionCallback.stateChangeComplete();
            return;
        }
        backstackDelegate.persistViewToState(root.getChildAt(0));
        root.removeAllViews();
        Key newKey = stateChange.topNewState();
        Context newContext = stateChange.createContext(this, newKey);
        View view = LayoutInflater.from(newContext).inflate(newKey.layout(), root, false);
        backstackDelegate.restoreViewFromState(view);
        root.addView(view);
        completionCallback.stateChangeComplete();
    }
}
