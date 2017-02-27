package com.zhuinden.simplestackdemonestedstack.application;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.BackstackDelegate;
import com.zhuinden.simplestack.HistoryBuilder;
import com.zhuinden.simplestack.SSLog;
import com.zhuinden.simplestack.ServiceFactory;
import com.zhuinden.simplestack.Services;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;
import com.zhuinden.simplestackdemonestedstack.R;
import com.zhuinden.simplestackdemonestedstack.presentation.paths.main.MainKey;
import com.zhuinden.simplestackdemonestedstack.presentation.paths.other.OtherKey;
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
        SSLog.addLogger(new SSLog.SSLogger() {
            @Override
            public void info(String tag, String message) {
                Log.i(tag, message);
            }
        });
        backstackDelegate = BackstackDelegate.configure().addServiceFactory(new ServiceFactory() {
            @Override
            public void bindServices(@NonNull Services.Builder builder) {
                Log.i("ServiceManager", "<[Build Services :: " + builder.getKey() + "]>");
            }

            @Override
            public void tearDownServices(@NonNull Services services) {
                Log.i("ServiceManager", "<[Tearing down :: " + services.getKey() + "]>");
            }
        }).build();
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
        SSLog.removeAllLoggers();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_main_next) {
            backstackDelegate.getBackstack().goTo(OtherKey.create());
            return true;
        }
        return super.onOptionsItemSelected(item);
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