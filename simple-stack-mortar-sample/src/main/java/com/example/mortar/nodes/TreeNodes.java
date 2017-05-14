package com.example.mortar.nodes;

import android.content.Context;

import com.zhuinden.servicetree.ServiceTree;

/**
 * Created by Zhuinden on 2017.05.13..
 */

public class TreeNodes {
    public static final String NODE_TAG = "SERVICE_TREE_NODE";

    private TreeNodes() {
    }

    public static ServiceTree.Node getNode(Context context) {
        ServiceTree.Node node = NodeContextWrapper.get(context);
        if(node == null) {
            node = NodeContextWrapper.get(context.getApplicationContext()); // <-- workaround also in Mortar
        }
        if(node == null) {
            throw new IllegalStateException("No node was found in context [" + context + "]");
        }
        return node;
    }
}
