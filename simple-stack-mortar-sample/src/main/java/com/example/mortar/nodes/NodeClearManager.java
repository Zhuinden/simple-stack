package com.example.mortar.nodes;

import android.support.annotation.NonNull;
import android.util.Log;

import com.zhuinden.servicetree.ServiceTree;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.StateChange;

/**
 * Created by Zhuinden on 2017.05.14..
 */

public class NodeClearManager
        implements Backstack.CompletionListener {
    private static final String TAG = "NodeClearManager";

    private final ServiceTree serviceTree;
    private final NodeStateManager nodeStateManager;

    public NodeClearManager(ServiceTree serviceTree, NodeStateManager nodeStateManager) {
        this.serviceTree = serviceTree;
        this.nodeStateManager = nodeStateManager;
    }

    @Override
    public void stateChangeCompleted(@NonNull StateChange stateChange) {
        for(Object previousKey : stateChange.getPreviousState()) {
            if(!stateChange.getNewState().contains(previousKey) && serviceTree.hasNodeWithKey(previousKey)) {
                Log.i(TAG, "Destroying [" + previousKey + " ]");
                nodeStateManager.clearStatesForKey(previousKey);
                serviceTree.removeNodeAndChildren(serviceTree.getNode(previousKey));
            }
        }
    }
}
