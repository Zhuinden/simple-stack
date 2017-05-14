package com.example.mortar.nodes;

import android.support.annotation.NonNull;
import android.util.Log;

import com.zhuinden.servicetree.ServiceTree;
import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.statebundle.StateBundle;

/**
 * Created by Zhuinden on 2017.05.14..
 */

public class NodeStateManager {
    private static final String TAG = "NodeStateManager";

    public static final String SERVICE_STATES = "SERVICE_STATES";

    private final ServiceTree serviceTree;

    public NodeStateManager(ServiceTree serviceTree) {
        this.serviceTree = serviceTree;
    }

    public StateBundle persistStates() {
        final StateBundle rootBundle = serviceTree.getRootService(SERVICE_STATES);
        serviceTree.traverseTree(ServiceTree.Walk.PRE_ORDER, true, new ServiceTree.Walk() {
            @Override
            public void execute(@NonNull ServiceTree.Node node, @NonNull CancellationToken cancellationToken) {
                StateBundle localBundle = rootBundle.getBundle(node.getKey().toString());
                if(localBundle == null) {
                    localBundle = new StateBundle();
                }
                for(ServiceTree.Node.Entry entry : node.getBoundServices()) {
                    Log.i(TAG, "Persisting state for [" + entry.getName() + "] in [" + node.getKey().toString() + "]");
                    if(entry.getService() instanceof Bundleable) {
                        localBundle.putBundle(entry.getName(), ((Bundleable) entry.getService()).toBundle());
                    }
                }
                rootBundle.putBundle(node.getKey().toString(), localBundle);
            }
        });
        return rootBundle;
    }

    public void restoreStatesForNode(ServiceTree.Node node) {
        StateBundle rootBundle = serviceTree.getRootService(SERVICE_STATES);
        StateBundle localBundle = rootBundle.getBundle(node.getKey().toString());
        Log.i(TAG, "Restoring state for [" + node.getKey().toString() + "] with bundle [" + localBundle + "]");
        for(ServiceTree.Node.Entry entry : node.getBoundServices()) {
            Log.i(TAG, "Restoring state for service [" + entry.getName() + "]");
            if(entry.getService() instanceof Bundleable) {
                ((Bundleable) entry.getService()).fromBundle(localBundle == null ? null : localBundle.getBundle(entry.getName()));
            }
        }
    }

    public void clearStatesForKey(Object previousKey) {
        final StateBundle rootBundle = serviceTree.getRootService(SERVICE_STATES);
        serviceTree.traverseSubtree(serviceTree.getNode(previousKey),
                ServiceTree.Walk.POST_ORDER,
                new ServiceTree.Walk() {
                    @Override
                    public void execute(@NonNull ServiceTree.Node node, @NonNull CancellationToken cancellationToken) {
                        Log.i(TAG, "Removing state for [" + node.getKey().toString() + "]");
                        rootBundle.remove(node.getKey().toString());
                    }
                });
    }
}
