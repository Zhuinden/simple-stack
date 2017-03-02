package com.zhuinden.simpleservicesexample.application;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.zhuinden.servicetree.ServiceTree;
import com.zhuinden.simpleservicesexample.R;
import com.zhuinden.simpleservicesexample.presentation.paths.a.A;
import com.zhuinden.simpleservicesexample.utils.ServiceLocator;
import com.zhuinden.simpleservicesexample.utils.ServiceManager;
import com.zhuinden.simpleservicesexample.utils.StackService;
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

    @BindView(R.id.root)
    RelativeLayout root;

    ServiceTree serviceTree;

    ServiceManager serviceManager;

    BackstackDelegate backstackDelegate;

    public static class NonConfigurationInstance {
        BackstackDelegate.NonConfigurationInstance backstackDelegate;
        ServiceManager serviceManager;

        private NonConfigurationInstance(BackstackDelegate.NonConfigurationInstance backstackDelegate, ServiceManager serviceManager) {
            this.backstackDelegate = backstackDelegate;
            this.serviceManager = serviceManager;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        NonConfigurationInstance nonConfigurationInstance = (NonConfigurationInstance) getLastCustomNonConfigurationInstance();
        if(nonConfigurationInstance != null) {
            serviceManager = nonConfigurationInstance.serviceManager;
            serviceTree = serviceManager.getServiceTree();
        } else {
            serviceTree = new ServiceTree();
            serviceManager = new ServiceManager(serviceTree);
        }

        backstackDelegate = new BackstackDelegate(null);
        backstackDelegate.onCreate(savedInstanceState,
                nonConfigurationInstance == null ? null : nonConfigurationInstance.backstackDelegate,
                HistoryBuilder.single(A.create()));
        if(savedInstanceState != null) {
            serviceManager.setRestoredStates(savedInstanceState.getParcelable(ServiceManager.SERVICE_STATES));
        }
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
        if(!backstackDelegate.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        backstackDelegate.onSaveInstanceState(outState);
        outState.putParcelable(ServiceManager.SERVICE_STATES, serviceManager.persistStates());
    }

    @Override
    protected void onPause() {
        backstackDelegate.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        backstackDelegate.onDestroy();
        super.onDestroy();
    }

    @Override
    public Object getSystemService(String name) {
        if(StackService.TAG.equals(name)) {
            return backstackDelegate.getBackstack();
        }
        if(StackService.DELEGATE_TAG.equals(name)) {
            return backstackDelegate;
        }
        if(ServiceLocator.SERVICE_TREE.equals(name)) {
            return serviceTree;
        }
        return super.getSystemService(name);
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
        Context newContext = stateChange.createContext(this, newKey);
        View view = LayoutInflater.from(newContext).inflate(newKey.layout(), root, false);
        backstackDelegate.restoreViewFromState(view);
        root.addView(view);
        completionCallback.stateChangeComplete();
    }

}
