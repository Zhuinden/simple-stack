package com.zhuinden.simpleservicesexample.utils;

import android.util.Log;

import com.zhuinden.servicetree.ServiceTree;
import com.zhuinden.simpleservicesexample.application.Key;
import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.simplestack.StateBundle;
import com.zhuinden.simplestack.StateChange;

import java.util.List;

/**
 * Created by Zhuinden on 2017.03.02..
 */

public class ServiceManager {
    private final ServiceTree serviceTree;

    public ServiceManager(ServiceTree serviceTree) {
        this.serviceTree = serviceTree;
    }

    public static final String SERVICE_STATES = "SERVICE_BUNDLE";

    private static final String TAG = "ServiceManager";

    public StateBundle persistStates() {
        StateBundle serviceStates = new StateBundle();
        serviceTree.traverseTree(ServiceTree.Walk.PRE_ORDER, node -> {
            StateBundle keyBundle = new StateBundle();
            for(ServiceTree.Node.Entry entry : node.getBoundServices()) {
                if(entry.getService() instanceof Bundleable) {
                    keyBundle.putParcelable(entry.getName(), ((Bundleable) entry.getService()).toBundle());
                }
            }
            serviceStates.putParcelable(node.getKey().toString(), keyBundle);
        });
        return serviceStates;
    }

    public void setupServices(StateChange stateChange) {
        // services
        StateBundle states = serviceTree.getRootService(SERVICE_STATES);
        for(Object _previousKey : stateChange.getPreviousState()) {
            Key previousKey = (Key) _previousKey;
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
            Key newKey = (Key) _newKey;
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
                    buildComposite(states, node, ((Composite) newKey));
                }
            }
        }
        // end services
    }

    private void buildComposite(StateBundle states, ServiceTree.Node parentNode, Composite composite) {
        for(Object _nestedKey : composite.keys()) {
            Key nestedKey = (Key) _nestedKey;
            ServiceTree.Node.Binder nestedBinder = serviceTree.createChildNode(parentNode, nestedKey);
            nestedKey.bindServices(nestedBinder);
            restoreServiceStateForKey(states, nestedKey, nestedBinder.get());
            if(nestedKey instanceof Composite) {
                buildComposite(states, nestedBinder.get(), (Composite) nestedKey);
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
                        ((Bundleable) entry.getService()).fromBundle(keyBundle.getParcelable(entry.getName()));
                    }
                }
            }
        }
    }

    public void setRestoredStates(StateBundle states) {
        serviceTree.registerRootService(SERVICE_STATES, states);
    }

    public ServiceTree getServiceTree() {
        return serviceTree;
    }
}
