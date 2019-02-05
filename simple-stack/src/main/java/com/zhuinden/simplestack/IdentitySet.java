package com.zhuinden.simplestack;

import java.util.IdentityHashMap;

/**
 * Storing the object as the key makes it be checked by its identity.
 *
 * @param <K> the object type
 */
class IdentitySet<K> extends IdentityHashMap<K, Integer> {
    private final static Integer PLACE_HOLDER = 0;

    public void add(K key) {
        put(key, PLACE_HOLDER);
    }
}
