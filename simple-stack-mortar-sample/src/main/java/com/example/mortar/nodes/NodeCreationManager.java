package com.example.mortar.nodes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.ViewGroup;

import com.example.mortar.util.Key;
import com.zhuinden.servicetree.ServiceTree;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.navigator.DefaultStateChanger;

/**
 * Created by Zhuinden on 2017.05.13..
 */

public class NodeCreationManager
        implements DefaultStateChanger.ContextCreationStrategy {
    private static final String TAG = "NodeCreationManager";

    private final ServiceTree serviceTree;
    private final ServiceTree.Node localRoot;
    private final NodeStateManager nodeStateManager;

    public NodeCreationManager(ServiceTree serviceTree, ServiceTree.Node localRoot, NodeStateManager nodeStateManager) {
        this.serviceTree = serviceTree;
        this.localRoot = localRoot;
        this.nodeStateManager = nodeStateManager;
    }

    @NonNull
    @Override
    public Context createContext(@NonNull Context baseContext, @NonNull Object newKey, @NonNull ViewGroup container, @NonNull StateChange stateChange) {
        ServiceTree.Node node;
        if(serviceTree.hasNodeWithKey(newKey)) {
            Log.i(TAG, "Obtaining scope node from tree [" + newKey + "]");
            node = serviceTree.getNode(newKey);
        } else {
            Log.i(TAG, "Creating new scope node for [" + newKey + "]");
            node = localRoot.createChild(newKey);
            if(newKey instanceof Key) {
                ((Key) newKey).bindServices(node);
            }
            nodeStateManager.restoreStatesForNode(node);
        }
        return new NodeContextWrapper(stateChange.createContext(baseContext, newKey), node);
    }
}
