package com.zhuinden.simplestack;

import android.support.annotation.NonNull;

import java.util.Map;

public class ScopeNode {
    private final ScopeManager scopeManager;

    private final Object key;
    private final String scopeTag;

    final Map<String, Object> scope; // this is package-private because it has to be.

    ScopeNode(ScopeManager scopeManager, Object key, String scopeTag, Map<String, Object> scope) {
        this.scopeManager = scopeManager;

        this.key = key;
        this.scopeTag = scopeTag;
        this.scope = scope;
    }

    /**
     * Returns the key that this service binder was created for.
     *
     * @param <T> the type of the key
     * @return the key
     */
    @NonNull
    public <T> T getKey() {
        //noinspection unchecked
        return (T) key;
    }

    /**
     * Returns the scope tag this service binder belongs to.
     *
     * @return the scope tag
     */
    @NonNull
    public final String getScopeTag() {
        return scopeTag;
    }

    /**
     * Returns whether the service with given service tag is in the local scope.
     *
     * @param serviceTag the service tag
     * @return if the service is in the scope
     */
    public boolean hasService(@NonNull String serviceTag) {
        if(serviceTag == null) {
            throw new IllegalArgumentException("Service tag cannot be null!");
        }
        return scope.containsKey(serviceTag);
    }

    /**
     * Retrieves the service from the local scope if it exists.
     *
     * @param serviceTag the service tag
     * @param <T>        the type of the service
     * @return the service
     * @throws IllegalArgumentException if the service is not in the scope
     */
    @NonNull
    public <T> T getService(@NonNull String serviceTag) {
        if(serviceTag == null) {
            throw new IllegalArgumentException("Service tag cannot be null!");
        }
        if(!hasService(serviceTag)) {
            throw new IllegalArgumentException("The service with tag [" + serviceTag + "] was not found!");
        }
        //noinspection unchecked
        return (T) scope.get(serviceTag);
    }

    /**
     * Returns whether the service can be found within the currently existing active scopes.
     *
     * @param serviceTag the service tag
     * @return if the service exists in active scopes
     */
    public boolean canFindService(@NonNull String serviceTag) {
        return scopeManager.canFindService(serviceTag);
    }

    /**
     * Retrieves the service from the active scopes if it exists.
     *
     * @param serviceTag the service tag
     * @param <T>        the type of the service
     * @return the service
     * @throws IllegalArgumentException if the service does not exist in the active scopes
     */
    @NonNull
    public <T> T lookupService(@NonNull String serviceTag) {
        return scopeManager.lookupService(serviceTag);
    }

    /**
     * Retrieves the {@link Backstack} that belongs to the {@link BackstackManager} who manages the scopes.
     *
     * @return the backstack
     */
    @NonNull
    public Backstack getBackstack() {
        return scopeManager.getBackstack();
    }
}
