package com.zhuinden.navigationexamplecond;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.Conductor;
import com.bluelinelabs.conductor.Router;
import com.zhuinden.simplestack.BackstackDelegate;
import com.zhuinden.simplestack.HistoryBuilder;
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

    BackstackDelegate backstackDelegate;
    Router router;

    ControllerStateChanger controllerStateChanger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        backstackDelegate = new BackstackDelegate(null);
        backstackDelegate.onCreate(savedInstanceState,
                getLastCustomNonConfigurationInstance(),
                HistoryBuilder.single(HomeKey.create()));
        backstackDelegate.registerForLifecycleCallbacks(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        navigation.setOnNavigationItemSelectedListener(item -> {
            switch(item.getItemId()) {
                case R.id.navigation_home:
                    replaceHistory(HomeKey.create());
                    return true;
                case R.id.navigation_dashboard:
                    replaceHistory(DashboardKey.create());
                    return true;
                case R.id.navigation_notifications:
                    replaceHistory(NotificationKey.create());
                    return true;
            }
            return false;
        });
        router = Conductor.attachRouter(this, root, savedInstanceState);
        controllerStateChanger = new ControllerStateChanger(router);
        backstackDelegate.setStateChanger(this);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return backstackDelegate.onRetainCustomNonConfigurationInstance();
    }

    @Override
    public void onBackPressed() {
        if(!backstackDelegate.onBackPressed()) {
            super.onBackPressed();
        }
    }

    private void replaceHistory(Object rootKey) {
        backstackDelegate.getBackstack().setHistory(HistoryBuilder.single(rootKey), StateChange.REPLACE);
    }

    public void navigateTo(Object key) {
        backstackDelegate.getBackstack().goTo(key);
    }

    @SuppressLint("WrongConstant")
    public static MainActivity get(Context context) {
        // noinspection ResourceType
        return (MainActivity)context.getSystemService(TAG);
    }

    @Override
    public Object getSystemService(@NonNull String name) {
        if(TAG.equals(name)) {
            return this;
        }
        return super.getSystemService(name);
    }

    @Override
    public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
        if(stateChange.topNewState().equals(stateChange.topPreviousState())) {
            completionCallback.stateChangeComplete();
            return;
        }
        controllerStateChanger.handleStateChange(stateChange);
        completionCallback.stateChangeComplete();
    }
}
