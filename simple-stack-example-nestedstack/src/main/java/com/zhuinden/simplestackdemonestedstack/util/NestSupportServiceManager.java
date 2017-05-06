package com.zhuinden.simplestackdemonestedstack.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.zhuinden.servicetree.ServiceTree;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.BackstackManager;
import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestackdemonestedstack.application.Key;
import com.zhuinden.statebundle.StateBundle;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class NestSupportServiceManager {
    public static final String SERVICE_MANAGER = "SERVICE_MANAGER";

    public static NestSupportServiceManager get(Context context) {
        // noinspection ResourceType
        return (NestSupportServiceManager) context.getSystemService(SERVICE_MANAGER);
    }

    private final ServiceTree serviceTree;

    private final List<Object> activeKeys = new ArrayList<>();

    public NestSupportServiceManager(StateBundle stateBundle) {
        this.serviceTree = new ServiceTree();
        if(stateBundle == null) {
            stateBundle = new StateBundle();
        }
        Log.i(SERVICE_MANAGER, "STATES :: " + stateBundle);
        serviceTree.registerRootService(SERVICE_STATES, stateBundle);
    }

    public static final String SERVICE_STATES = "SERVICE_BUNDLE";

    public StateBundle persistStates() {
        final StateBundle serviceStates = new StateBundle();
        serviceTree.traverseTree(ServiceTree.Walk.PRE_ORDER, new ServiceTree.Walk() {
            @Override
            public void execute(@NonNull ServiceTree.Node node, @NonNull CancellationToken cancellationToken) {
                StateBundle keyBundle = new StateBundle();
                for(ServiceTree.Node.Entry entry : node.getBoundServices()) {
                    if(entry.getService() instanceof Bundleable) {
                        keyBundle.putParcelable(entry.getName(), ((Bundleable) entry.getService()).toBundle());
                    }
                }
                serviceStates.putParcelable(node.getKey().toString(), keyBundle);
            }
        });
        return serviceStates;
    }

    public void setupServices(StateChange stateChange) {
        final StateBundle states = serviceTree.getRootService(SERVICE_STATES);
        for(Object previousKey : stateChange.getPreviousState()) {
            if(!stateChange.getNewState().contains(previousKey)) {
                activeKeys.remove(previousKey);
            }
        }
        for(Object newKey : stateChange.getNewState()) {
            activeKeys.remove(newKey);
            if(newKey == stateChange.topNewState()) {
                activeKeys.add(newKey);
            }
            buildServices(newKey);
        }

        final Set<Object> keysToKeep = new LinkedHashSet<>();
        for(Object newKey : stateChange.getNewState()) {
            if(serviceTree.hasNodeWithKey(newKey)) {
                ServiceTree.Node root = serviceTree.findRoot(serviceTree.getNode(newKey));
                serviceTree.traverseSubtree(root, ServiceTree.Walk.PRE_ORDER, new ServiceTree.Walk() {
                    @Override
                    public void execute(@NonNull ServiceTree.Node node, @NonNull CancellationToken cancellationToken) {
                        keysToKeep.add(node.getKey());
                    }
                });
            }
        }
        for(Object activeKey : activeKeys) {
            if(serviceTree.hasNodeWithKey(activeKey)) {
                ServiceTree.Node root = serviceTree.findRoot(serviceTree.getNode(activeKey));
                serviceTree.traverseSubtree(root, ServiceTree.Walk.PRE_ORDER, new ServiceTree.Walk() {
                    @Override
                    public void execute(@NonNull ServiceTree.Node node, @NonNull CancellationToken cancellationToken) {
                        keysToKeep.add(node.getKey());
                    }
                });
            }
        }
        serviceTree.traverseTree(ServiceTree.Walk.POST_ORDER, new ServiceTree.Walk() {
            @Override
            public void execute(@NonNull ServiceTree.Node node, @NonNull CancellationToken cancellationToken) {
                if(!keysToKeep.contains(node.getKey())) {
                    states.remove(node.getKey().toString());
                    serviceTree.removeNodeAndChildren(node);
                }
            }
        });
    }

    private void buildServices(Object newKey) {
        if(!serviceTree.hasNodeWithKey(newKey)) {
            ServiceTree.Node node;
            if(!(newKey instanceof Child)) {
                node = serviceTree.createRootNode(newKey);
            } else {
                Object parentKey = ((Child) newKey).parent();
                if(!serviceTree.hasNodeWithKey(parentKey)) {
                    buildServices(parentKey);
                }
                node = serviceTree.createChildNode(serviceTree.getNode(parentKey), newKey);
            }
            buildServicesForKey(newKey, node);
        }
    }

    private void buildServicesForKey(Object newKey, ServiceTree.Node node) {
        ((Key) newKey).bindServices(node);
        restoreServiceStateForKey(node);
        if(newKey instanceof Composite) {
            for(Object nestedKey : ((Composite) newKey).keys()) {
                ServiceTree.Node nestedNode = serviceTree.createChildNode(node, nestedKey);
                buildServicesForKey(nestedKey, nestedNode);
            }
        }
        if(((Key) newKey).hasNestedStack()) {
            Backstack nestedStack = serviceTree.getNode(newKey).<BackstackManager>getService(Key.NESTED_STACK).getBackstack();
            for(Object childKey : nestedStack.getInitialParameters()) {
                buildServices(childKey);
            }
        }
    }

    private void restoreServiceStateForKey(ServiceTree.Node node) {
        StateBundle states = serviceTree.getRootService(SERVICE_STATES);
        StateBundle keyBundle = states.getParcelable(node.getKey().toString());
        if(keyBundle != null) {
            List<ServiceTree.Node.Entry> entries = node.getBoundServices();
            for(ServiceTree.Node.Entry entry : entries) {
                if(entry.getService() instanceof Bundleable) {
                    ((Bundleable) entry.getService()).fromBundle(keyBundle.<StateBundle>getParcelable(entry.getName()));
                }
            }
        }
    }

    public ServiceTree getServiceTree() {
        return serviceTree;
    }

    public boolean handleBack(final Backstack backstack) {
        Object lastKey = activeKeys.get(activeKeys.size() - 1);
        class Cancellation {
            private boolean cancelled;
        }
        final Cancellation cancellation = new Cancellation();
        serviceTree.traverseChain(serviceTree.getNode(lastKey), new ServiceTree.Walk() {
            @Override
            public void execute(@NonNull ServiceTree.Node node, @NonNull CancellationToken cancellationToken) {
                Object _key = node.getKey();
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
