package com.zhuinden.simplestackdemonestedstack.util;

import android.content.Context;

import com.zhuinden.servicetree.ServiceTree;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.BackstackManager;
import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.navigator.Navigator;
import com.zhuinden.simplestackdemonestedstack.application.Key;
import com.zhuinden.statebundle.StateBundle;

import java.util.ArrayList;
import java.util.List;

public class NestSupportServiceManager {
    public static final String SERVICE_MANAGER = "SERVICE_MANAGER";

    public static NestSupportServiceManager get(Context context) {
        //noinspection ResourceType
        return (NestSupportServiceManager) context.getSystemService(SERVICE_MANAGER);
    }

    private final ServiceTree serviceTree;

    private final List<Object> activeKeys = new ArrayList<>();

    public NestSupportServiceManager(ServiceTree serviceTree) {
        this.serviceTree = serviceTree;
    }

    public static final String SERVICE_STATES = "SERVICE_BUNDLE";

    private static final String TAG = "NestServiceManager";

    public StateBundle persistStates() {
        StateBundle serviceStates = new StateBundle();
        serviceTree.traverseTree(ServiceTree.Walk.PRE_ORDER, (node, cancellationToken) -> {
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
        setupServices(stateChange, false);
    }

    public void setupServices(StateChange stateChange, boolean isFromCompositeKey) {
        StateBundle states = serviceTree.getRootService(SERVICE_STATES);
        for(Object _previousKey : stateChange.getPreviousState()) {
            Key previousKey = (Key) _previousKey;
            if(!stateChange.getNewState().contains(previousKey)) {
                activeKeys.remove(previousKey);
                if(!isFromCompositeKey) {
                    ServiceTree.Node previousNode = serviceTree.getNode(previousKey);
                    if(states != null) {
                        serviceTree.traverseSubtree(previousNode, ServiceTree.Walk.POST_ORDER, (node, cancellationToken) -> {
                            states.remove(node.getKey().toString());
                        });
                    }
                    serviceTree.removeNodeAndChildren(previousNode);
                }
            }
        }
        for(Object _newKey : stateChange.getNewState()) {
            Key newKey = (Key) _newKey;
            activeKeys.remove(newKey);
            if(newKey == stateChange.topNewState()) {
                activeKeys.add(newKey);
            }
            if(!isFromCompositeKey) {
                buildServices(states, newKey);
            }
        }
    }

    private void buildServices(StateBundle states, Key newKey) {
        if(!serviceTree.hasNodeWithKey(newKey)) {
            ServiceTree.Node node = newKey instanceof Child ? serviceTree.createChildNode(serviceTree.getNode(((Child) newKey).parent()),
                    newKey) //
                    : serviceTree.createRootNode(newKey);
            buildServicesForKey(states, newKey, node);
        }
    }

    private void buildServicesForKey(StateBundle states, Key newKey, ServiceTree.Node node) {
        newKey.bindServices(node);
        restoreServiceStateForKey(states, newKey, node);
        if(newKey instanceof Composite) {
            for(Object _nestedKey : ((Composite) newKey).keys()) {
                Key nestedKey = (Key) _nestedKey;
                ServiceTree.Node nestedNode = serviceTree.createChildNode(node, nestedKey);
                buildServicesForKey(states, (Key) _nestedKey, nestedNode);
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

    public boolean handleBack(Context context) {
        ServiceTree serviceTree = ServiceLocator.getService(context, ServiceLocator.SERVICE_TREE);
        Object lastKey = activeKeys.get(activeKeys.size() - 1);
        Backstack backstack = Navigator.getBackstack(context);
        class Cancellation {
            private boolean cancelled;
        }
        Cancellation cancellation = new Cancellation();
        serviceTree.traverseChain(serviceTree.getNode(lastKey), (node, cancellationToken) -> {
            Object _key = node.getKey();
            if(_key instanceof Key) { // ROOT is defined by Activity's TAG
                Key key = (Key) _key;
                if(key.hasNestedStack()) {
                    BackstackManager backstackManager = serviceTree.getNode(key).getService(Key.NESTED_STACK);
                    if(backstackManager.getBackstack().goBack()) {
                        cancellation.cancelled = true;
                        cancellationToken.cancel();
                    }
                }
            }
        });
        if(cancellation.cancelled) {
            return true;
        }
        return backstack.goBack();
    }
}
