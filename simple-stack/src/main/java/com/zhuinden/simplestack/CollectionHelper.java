package com.zhuinden.simplestack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * Helper class to reimplement `retainAll()`, as its behavior is broken for LinkedHashSet on Android 6 and Android 6.1.
 * <p>
 * See issue #256.
 */
class CollectionHelper {
    private CollectionHelper() {
    }

    public static boolean retainAll(Set<?> mutableSet, Collection<?> collection) {
        final ArrayList<?> arrayList = new ArrayList<>(mutableSet);
        boolean modified = false;
        for(Object item : arrayList) {
            if(!collection.contains(item)) {
                mutableSet.remove(item);
                modified = true;
            }
        }
        return modified;
    }
}
