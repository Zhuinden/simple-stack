package com.zhuinden.navigationexamplecond.application;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.Conductor;
import com.bluelinelabs.conductor.Router;
import com.zhuinden.navigationexamplecond.R;
import com.zhuinden.navigationexamplecond.core.navigation.ControllerStateChanger;
import com.zhuinden.navigationexamplecond.screens.DashboardKey;
import com.zhuinden.navigationexamplecond.screens.HomeKey;
import com.zhuinden.navigationexamplecond.screens.NotificationKey;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.History;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity
        extends AppCompatActivity
        implements StateChanger {
    private static final String TAG = "MainActivity";

    @BindView(R.id.navigation)
    BottomNavigationView navigation;

    @BindView(R.id.root)
    ViewGroup root;

    Backstack backstack;
    Router router;

    ControllerStateChanger controllerStateChanger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(getLastCustomNonConfigurationInstance() != null) {
            backstack = (Backstack) getLastCustomNonConfigurationInstance();
        } else {
            backstack = new Backstack();
            backstack.setup(History.of(HomeKey.create()));
            if(savedInstanceState != null) {
                backstack.fromBundle(savedInstanceState.getParcelable("backstack"));
            }
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        navigation.setOnNavigationItemSelectedListener(item -> {
            switch(item.getItemId()) {
                case R.id.navigation_home:
                    backstack.setHistory(History.of(HomeKey.create()), StateChange.REPLACE);
                    return true;
                case R.id.navigation_dashboard:
                    backstack.setHistory(History.of(DashboardKey.create()), StateChange.REPLACE);
                    return true;
                case R.id.navigation_notifications:
                    backstack.setHistory(History.of(NotificationKey.create()), StateChange.REPLACE);
                    return true;
            }
            return false;
        });
        router = Conductor.attachRouter(this, root, savedInstanceState);
        controllerStateChanger = new ControllerStateChanger(router);
        backstack.setStateChanger(this);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        backstack.reattachStateChanger();
    }

    @Override
    protected void onPause() {
        backstack.detachStateChanger();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("backstack", backstack.toBundle());
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
    protected void onDestroy() {
        super.onDestroy();

        backstack.executePendingStateChange();
        if(isFinishing()) {
            backstack.finalizeScopes();
        }
    }

    @Override
    public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
        if(stateChange.isTopNewKeyEqualToPrevious()) {
            completionCallback.stateChangeComplete();
            return;
        }
        controllerStateChanger.handleStateChange(stateChange);
        completionCallback.stateChangeComplete();
    }

    public Backstack getBackstack() {
        return backstack;
    }
}
