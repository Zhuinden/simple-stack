package com.zhuinden.simplestack;

import android.support.annotation.NonNull;

/**
 * Created by Zhuinden on 2017.02.22..
 */

class HierarchyServiceFactory
        extends ServiceFactory {
    private final KeyParceler keyParceler;

    public HierarchyServiceFactory(KeyParceler keyParceler) {
        this.keyParceler = keyParceler;
    }

    public void bindServices(@NonNull Services.Builder builder) {
        Object parentKey = builder.getService(BackstackManager.LOCAL_KEY);
        if(parentKey != null) {
            builder.withService(BackstackManager.PARENT_KEY, parentKey);
        }
        builder.withService(BackstackManager.LOCAL_KEY, builder.getKey());
        NestedStack parentStack = builder.getService(BackstackManager.LOCAL_STACK);
        if(parentStack == null) {
            parentStack = builder.getService(BackstackManager.ROOT_STACK);
        }
        builder.withService(BackstackManager.LOCAL_STACK, new NestedStack(parentKey, parentStack, keyParceler));
    }
}
