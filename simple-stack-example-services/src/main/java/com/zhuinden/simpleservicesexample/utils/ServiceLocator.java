package com.zhuinden.simpleservicesexample.utils;

import android.content.Context;

import com.zhuinden.servicetree.ServiceTree;
import com.zhuinden.simpleservicesexample.application.Key;
import com.zhuinden.simplestack.Backstack;

/**
 * Created by Zhuinden on 2017.02.18..
 */

public class ServiceLocator {
    public static final String SERVICE_TREE = "SERVICE_TREE";

    public static <T> T getService(Context context, String name) {
        T t = (T) context.getSystemService(name);
        if(t == null) {
            // noinspection ResourceType
            ServiceTree serviceTree = (ServiceTree)context.getSystemService(SERVICE_TREE);
            ServiceTree.Node node = serviceTree.getNode(Backstack.getKey(context));
            return node.getService(name);
        }
        return t;
    }
}
