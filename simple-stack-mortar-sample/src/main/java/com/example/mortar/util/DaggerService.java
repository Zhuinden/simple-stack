package com.example.mortar.util;

import android.content.Context;

import com.zhuinden.servicetree.ServiceTree;

/**
 * Created by Zhuinden on 2017.05.13..
 */

public class DaggerService {
    public static final String SERVICE_NAME = "DAGGER_SERVICE";

    public static <T> T get(ServiceTree.Node node) {
        return node.getService(SERVICE_NAME);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(Context context) {
        // noinspection ResourceType
        T t = (T) context.getSystemService(SERVICE_NAME);

        // Mortar workaround
        if(t == null) { // <-- activity base context is not yet set, and we need to look this up from application
            // noinspection ResourceType
            t = (T) context.getApplicationContext().getSystemService(SERVICE_NAME); // <-- workaround also in mortar
        }
        return t;
    }
}
