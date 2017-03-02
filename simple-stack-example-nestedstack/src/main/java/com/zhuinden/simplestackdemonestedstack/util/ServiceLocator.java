package com.zhuinden.simplestackdemonestedstack.util;

import android.content.Context;

import com.zhuinden.servicetree.ServiceTree;
import com.zhuinden.simplestack.Backstack;

/**
 * Created by Zhuinden on 2017.02.19..
 */

public class ServiceLocator {
    public static final String SERVICE_TREE = "SERVICE_TREE";

    public static <T> T getService(Context context, String name) {
        // noinspection unchecked
        T service = (T) context.getSystemService(name);
        if(service == null) {
            // noinspection ResourceType
            ServiceTree serviceTree = (ServiceTree)context.getSystemService(SERVICE_TREE);
            ServiceTree.Node node = serviceTree.getNode(Backstack.getKey(context));
            return node.getService(name);
        }
        return service;
    }
}
