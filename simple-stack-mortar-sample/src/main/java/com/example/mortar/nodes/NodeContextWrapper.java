package com.example.mortar.nodes;

import android.content.Context;
import android.content.ContextWrapper;
import android.view.LayoutInflater;

import com.zhuinden.servicetree.ServiceTree;

/**
 * Created by Zhuinden on 2017.05.13..
 */

public class NodeContextWrapper extends ContextWrapper {
    private LayoutInflater layoutInflater;

    static ServiceTree.Node get(Context context) {
        // noinspection ResourceType
        return (ServiceTree.Node)context.getSystemService(TreeNodes.NODE_TAG);
    }

    private final ServiceTree.Node node;

    public NodeContextWrapper(Context base, ServiceTree.Node node) {
        super(base);
        this.node = node;
    }

    @Override
    public Object getSystemService(String name) {
        if(Context.LAYOUT_INFLATER_SERVICE.equals(name)) {
            if(layoutInflater == null) {
                layoutInflater = LayoutInflater.from(getBaseContext()).cloneInContext(this);
            }
            return layoutInflater;
        }
        if(TreeNodes.NODE_TAG.equals(name)) {
            return node;
        }
        if(node.hasService(name)) {
            return node.getService(name);
        }
        return super.getSystemService(name);
    }
}
