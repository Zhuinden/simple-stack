package com.zhuinden.simpleservicesexample.application;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.zhuinden.servicetree.ServiceTree;
import com.zhuinden.simpleservicesexample.R;
import com.zhuinden.simpleservicesexample.presentation.paths.a.A;
import com.zhuinden.simpleservicesexample.utils.Child;
import com.zhuinden.simpleservicesexample.utils.Composite;
import com.zhuinden.simpleservicesexample.utils.ServiceLocator;
import com.zhuinden.simpleservicesexample.utils.StackService;
import com.zhuinden.simplestack.BackstackDelegate;
import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.simplestack.HistoryBuilder;
import com.zhuinden.simplestack.StateBundle;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity
        extends AppCompatActivity
        implements StateChanger {
    public static final String SERVICE_STATES = "SERVICE_BUNDLE";

    private static final String TAG = "MainActivity";

    @BindView(R.id.root)
    RelativeLayout root;

    ServiceTree serviceTree;

    BackstackDelegate backstackDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        serviceTree = new ServiceTree();

        backstackDelegate = new BackstackDelegate(null);
        backstackDelegate.onCreate(savedInstanceState,
                getLastCustomNonConfigurationInstance(),
                HistoryBuilder.single(A.create()));
        if(savedInstanceState != null) {
            serviceTree.registerRootService(SERVICE_STATES, savedInstanceState.getParcelable(SERVICE_STATES));
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
        StateBundle serviceStates = new StateBundle();
        serviceTree.traverseTree(ServiceTree.Walk.PRE_ORDER, node -> {
            StateBundle keyBundle = new StateBundle();
            for(ServiceTree.Node.Entry entry : node.getBoundServices()) {
                if(entry.getService() instanceof Bundleable) {
                    keyBundle.putParcelable(entry.getName(), ((Bundleable)entry.getService()).toBundle());
                }
            }
            serviceStates.putParcelable(node.getKey().toString(), keyBundle);
        });
        outState.putParcelable(SERVICE_STATES, serviceStates);
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
        setupServices(stateChange);

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

    private void setupServices(StateChange stateChange) {
        // services
        StateBundle states = serviceTree.getRootService(SERVICE_STATES);
        for(Object _previousKey : stateChange.getPreviousState()) {
            Key previousKey = (Key)_previousKey;
            if(!stateChange.getNewState().contains(previousKey)) {
                ServiceTree.Node previousNode = serviceTree.getNode(previousKey);
                if(states != null) {
                    serviceTree.traverseSubtree(previousNode, ServiceTree.Walk.POST_ORDER, node -> {
                        states.remove(node.getKey().toString());
                        Log.i(TAG, "Destroy [" + node + "]");
                    });
                }
                serviceTree.removeNodeAndChildren(previousNode);
            }
        }
        for(Object _newKey : stateChange.getNewState()) {
            Key newKey = (Key)_newKey;
            if(!serviceTree.hasNodeWithKey(newKey)) {
                ServiceTree.Node.Binder binder;
                if(newKey instanceof Child) {
                    binder = serviceTree.createChildNode(serviceTree.getNode(((Child) newKey).parent()), newKey);
                } else {
                    binder = serviceTree.createRootNode(newKey);
                }
                newKey.bindServices(binder);
                ServiceTree.Node node = binder.get();
                restoreServiceStateForKey(states, newKey, node);
                if(newKey instanceof Composite) {
                    buildComposite(states, node, ((Composite)newKey));
                }
            }
        }
        // end services
    }

    private void buildComposite(StateBundle states, ServiceTree.Node parentNode, Composite composite) {
        for(Object _nestedKey : composite.keys()) {
            Key nestedKey = (Key)_nestedKey;
            ServiceTree.Node.Binder nestedBinder = serviceTree.createChildNode(parentNode, nestedKey);
            nestedKey.bindServices(nestedBinder);
            restoreServiceStateForKey(states, nestedKey, nestedBinder.get());
            if(nestedKey instanceof Composite) {
                buildComposite(states, nestedBinder.get(), (Composite)nestedKey);
            }
        }
    }

    private void restoreServiceStateForKey(StateBundle states, Key key, ServiceTree.Node node) {
        if(states != null) {
            StateBundle keyBundle = states.getParcelable(key.toString());
            if(keyBundle != null) {
                List<ServiceTree.Node.Entry> entries = node.getBoundServices();
                for(ServiceTree.Node.Entry entry : entries) {
                    if(entry.getService() instanceof Bundleable) {
                        ((Bundleable)entry.getService()).fromBundle(keyBundle.getParcelable(entry.getName()));
                    }
                }
            }
        }
    }
}
