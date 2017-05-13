package com.example.mortar.util;

import android.content.Context;

import com.example.mortar.core.MortarDemoActivity;
import com.example.mortar.core.SingletonComponent;
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
        return (T)context.getSystemService(SERVICE_NAME);
    }
}
