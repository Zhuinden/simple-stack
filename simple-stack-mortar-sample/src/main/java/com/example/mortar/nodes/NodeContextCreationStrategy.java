package com.example.mortar.nodes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.example.mortar.util.Key;
import com.zhuinden.servicetree.ServiceTree;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.navigator.DefaultStateChanger;

/**
 * Created by Zhuinden on 2017.05.13..
 */

public class NodeContextCreationStrategy implements DefaultStateChanger.ContextCreationStrategy {
    private final ServiceTree serviceTree;
    private final ServiceTree.Node localRoot;

    public NodeContextCreationStrategy(ServiceTree serviceTree, ServiceTree.Node localRoot) {
        this.serviceTree = serviceTree;
        this.localRoot = localRoot;
    }

    @NonNull
    @Override
    public Context createContext(@NonNull Context baseContext, @NonNull Object newKey, @NonNull ViewGroup container, @NonNull StateChange stateChange) {
        ServiceTree.Node node;
        if(serviceTree.hasNodeWithKey(newKey)) {
            node = serviceTree.getNode(newKey);
        } else {
            node = localRoot.createChild(newKey);
            if(newKey instanceof Key) {
                ((Key) newKey).bindServices(node);
            }
        }
        return new NodeContextWrapper(stateChange.createContext(baseContext, newKey), node);
    }
}
