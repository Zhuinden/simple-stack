package com.zhuinden.simplestackdemonestedstack.application;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.zhuinden.servicetree.ServiceTree;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.BackstackDelegate;
import com.zhuinden.simplestack.HistoryBuilder;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;
import com.zhuinden.simplestackdemonestedstack.R;
import com.zhuinden.simplestackdemonestedstack.presentation.paths.main.MainKey;
import com.zhuinden.simplestackdemonestedstack.presentation.paths.other.OtherKey;
import com.zhuinden.simplestackdemonestedstack.util.BackPressListener;
import com.zhuinden.simplestackdemonestedstack.util.NestSupportServiceManager;
import com.zhuinden.simplestackdemonestedstack.util.PreserveTreeScopesStrategy;
import com.zhuinden.simplestackdemonestedstack.util.ServiceLocator;
import com.zhuinden.statebundle.StateBundle;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity
        extends AppCompatActivity
        implements StateChanger {
    public static final String TAG = "MainActivity";

    public static MainActivity get(Context context) {
        //noinspection ResourceType
        return (MainActivity)context.getSystemService(TAG);
    }

    @BindView(R.id.view_root)
    FrameLayout root;

    BackstackDelegate backstackDelegate;
    Backstack backstack;

    static class NonConfigurationInstance {
        NestSupportServiceManager serviceManager;
        BackstackDelegate.NonConfigurationInstance backstackDelegateNonConfig;

        private NonConfigurationInstance(NestSupportServiceManager serviceManager, BackstackDelegate.NonConfigurationInstance backstackDelegateNonConfig) {
            this.serviceManager = serviceManager;
            this.backstackDelegateNonConfig = backstackDelegateNonConfig;
        }
    }

    NestSupportServiceManager serviceManager;
    ServiceTree serviceTree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        NonConfigurationInstance nonConfigurationInstance = (NonConfigurationInstance)getLastCustomNonConfigurationInstance();
        if(nonConfigurationInstance != null) {
            serviceManager = nonConfigurationInstance.serviceManager;
            serviceTree = serviceManager.getServiceTree();
        } else {
            serviceTree = new ServiceTree();
            serviceTree.createRootNode(TAG);
            serviceManager = new NestSupportServiceManager(serviceTree, TAG);
            if(savedInstanceState != null) {
                serviceManager.setRestoredStates(savedInstanceState.getParcelable("SERVICE_BUNDLE"));
            } else {
                serviceManager.setRestoredStates(new StateBundle());
            }
        }
        backstackDelegate = new BackstackDelegate(null);
        backstackDelegate.setStateClearStrategy(new PreserveTreeScopesStrategy(serviceTree));
        backstackDelegate.onCreate(savedInstanceState, nonConfigurationInstance == null ? null : nonConfigurationInstance.backstackDelegateNonConfig, HistoryBuilder.single(MainKey.create()));
        backstack = backstackDelegate.getBackstack();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        backstackDelegate.setStateChanger(this);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return new NonConfigurationInstance(serviceManager, backstackDelegate.onRetainCustomNonConfigurationInstance());
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
        outState.putParcelable("SERVICE_BUNDLE", serviceManager.persistStates());
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
        if(ServiceLocator.SERVICE_TREE.equals(name)) {
            return serviceTree;
        }
        if(NestSupportServiceManager.SERVICE_MANAGER.equals(name)) {
            return serviceManager;
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
        serviceManager.setupServices(stateChange);

        if(stateChange.topNewState().equals(stateChange.topPreviousState())) {
            completionCallback.stateChangeComplete();
            return;
        }
        backstackDelegate.persistViewToState(root.getChildAt(0));
        root.removeAllViews();
        Key newKey = stateChange.topNewState();
        View newView = LayoutInflater.from(stateChange.createContext(this, newKey)).inflate(newKey.layout(), root, false);
        backstackDelegate.restoreViewFromState(newView);
        root.addView(newView);
        completionCallback.stateChangeComplete();
    }
}